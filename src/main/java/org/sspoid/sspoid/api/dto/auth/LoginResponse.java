package org.sspoid.sspoid.api.dto.auth;

import lombok.Builder;

@Builder
public record LoginResponse (
        String accessToken,
        String refreshToken,
        String nickname
){
}
