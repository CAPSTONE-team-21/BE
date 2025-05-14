package org.sspoid.sspoid.api.dto.auth;

import lombok.Builder;

@Builder
public record TokenResponse (
        String accessToken,
        String refreshToken
){
}
