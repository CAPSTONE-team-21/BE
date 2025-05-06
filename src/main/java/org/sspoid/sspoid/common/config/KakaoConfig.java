package org.sspoid.sspoid.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KakaoConfig {

    @Value("${kakao.api-key}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String getTokenUrl(){
        return "https://kauth.kakao.com";
    }
    public String getUserInfoUrl(){
        return "https://kapi.kakao.com";
    }

}
