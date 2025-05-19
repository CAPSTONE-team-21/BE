package org.sspoid.sspoid.api.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sspoid.sspoid.api.dto.auth.KakaoLoginRequest;
import org.sspoid.sspoid.api.dto.auth.KakaoUserInfoResponse;
import org.sspoid.sspoid.api.service.KakaoLoginService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/api/login/kakao")
    public ResponseEntity<?> callback(@Valid @RequestBody KakaoLoginRequest request) {
        log.info("[POST] Received code from front: {}", request.code());

        String accessToken = kakaoLoginService.getAccessToken(request.code());
        KakaoUserInfoResponse userInfo = kakaoLoginService.getUserInfo(accessToken);

        return ResponseEntity.ok(userInfo);
    }

//    // ✅ 테스트용: 카카오 redirect_uri로 직접 GET 요청 받을 수 있도록 처리
//    @GetMapping("/api/login/kakao")
//    public ResponseEntity<?> getCallback(@RequestParam("code") String code) {
//        log.info("[GET] Received code from Kakao: {}", code);
//        String accessToken = kakaoLoginService.getAccessToken(code);
//        KakaoUserInfoResponse userInfo = kakaoLoginService.getUserInfo(accessToken);
//        return ResponseEntity.ok(userInfo);
//    }
}
