package org.sspoid.sspoid.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Sspoid Local Server"),
                        new Server().url("http://43.203.173.135").description("Sspoid Production Server")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("Sspoid Swagger");
    }
}
