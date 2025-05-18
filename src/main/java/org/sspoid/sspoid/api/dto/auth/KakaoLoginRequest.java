package org.sspoid.sspoid.api.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest (
        @NotBlank(message = "Authorization Code는 필수 입력 항목입니다.")
        String code
){
}
