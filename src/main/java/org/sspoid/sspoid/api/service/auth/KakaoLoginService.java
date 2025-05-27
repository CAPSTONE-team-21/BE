package org.sspoid.sspoid.api.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.sspoid.sspoid.api.dto.auth.KakaoUserInfoResponse;
import org.sspoid.sspoid.api.dto.auth.KakoTokenResponse;
import org.sspoid.sspoid.common.config.KakaoConfig;
import org.sspoid.sspoid.db.user.User;
import org.sspoid.sspoid.db.user.UserRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoConfig kakaoConfig;
    private final UserRepository userRepository;

    public String getAccessToken(String code) {
        log.info("🔑 인가 코드: {}", code);
        log.info("🔧 client_id: {}", kakaoConfig.getClientId());
        log.info("🔧 redirect_uri: {}", kakaoConfig.getRedirectUri());

        KakoTokenResponse response = WebClient.create(kakaoConfig.getTokenUrl())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoConfig.getClientId())
                        .queryParam("redirect_uri", kakaoConfig.getRedirectUri())
                        .queryParam("code", code)
                        .build()
                )
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> {
                    log.error("❌ 토큰 요청 실패 상태 코드: {}", r.statusCode());
                    return Mono.error(new RuntimeException("토큰 발급 실패"));
                })
                .bodyToMono(KakoTokenResponse.class)
                .block();

        log.info("엑세스 토큰 수신됨: {}", response.getAccessToken());  // ✅ 토큰 확인

        saveUserInfo(getUserInfo(response.getAccessToken()));

        return response.getAccessToken();
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        return WebClient.create(kakaoConfig.getUserInfoUrl())
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> Mono.error(new RuntimeException("사용자 정보 조회 실패")))
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();
    }

    public KakaoUserInfoResponse saveUserInfo(KakaoUserInfoResponse kakaoUserInfoResponse) {
        User user = User.builder()
                .email(kakaoUserInfoResponse.getKakaoAccount().getEmail())
                .password(null)
                .name(kakaoUserInfoResponse.getKakaoAccount().getProfile().getNickname())
                .build();
        userRepository.save(user);
        return kakaoUserInfoResponse;
    }
}
