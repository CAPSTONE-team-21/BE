package org.sspoid.sspoid.api.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        String accessToken = kakaoLoginService.getAccessToken(request.code());
        KakaoUserInfoResponse userInfo = kakaoLoginService.getUserInfo(accessToken);

        return ResponseEntity.ok(userInfo);
    }

}
