package org.sspoid.sspoid.api.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.sspoid.sspoid.api.dto.KakaoUserInfoResponse;
import org.sspoid.sspoid.api.service.KakaoLoginService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/api/login/kakao")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = kakaoLoginService.getAccessToken(code);
        KakaoUserInfoResponse userInfo = kakaoLoginService.getUserInfo(accessToken);

        return ResponseEntity.ok(userInfo);
    }

}
