package org.sspoid.sspoid.api.service;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.sspoid.sspoid.api.dto.model.ChatModelResponse;
import org.sspoid.sspoid.api.dto.model.ModelPromptRequest;
import org.sspoid.sspoid.db.chatsession.SkinGroup;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CallApiService {

    private final WebClient webClient;

    @Value("${model.api.url}")
    private String ChatModel_URL;

    public CallApiService() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000) // 연결 시도 타임아웃: 1분
                .responseTimeout(Duration.ofSeconds(600))             // 응답 수신 타임아웃: 10분
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(300, TimeUnit.SECONDS))   // 읽기 타임아웃
                        .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))  // 쓰기 타임아웃
                );

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public String callChatModelApi(String message, SkinGroup skinGroup) {
        try {
            ModelPromptRequest request = new ModelPromptRequest(message, skinGroup.name());
            System.out.println("📤 [모델 요청] SkinGroup: " + skinGroup.name() + " | Message: " + message);

            ChatModelResponse response = webClient.post()
                    .uri(ChatModel_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),  // ✅ 직접 람다로 체크
                            clientResponse -> clientResponse.bodyToMono(String.class).map(errorBody -> {
                                System.err.println("❌ [모델 응답 오류] Status: " + clientResponse.statusCode() + " | Body: " + errorBody);
                                return new RuntimeException("모델 응답 오류: " + errorBody);
                            })
                    )
                    .bodyToMono(ChatModelResponse.class)
                    .doOnNext(res -> System.out.println("📥 [모델 응답 수신 완료] 응답 메시지 길이: " + res.message().length()))
                    .block();

            if (response == null || response.message() == null) {
                System.err.println("⚠️ [모델 응답 없음 또는 null] message=null");
                throw new RuntimeException("모델 응답이 null입니다");
            }

            return response.message();
        }
        catch (Exception e) {
            System.err.println("🔥 [모델 API 호출 실패] 에러: " + e.getMessage());
            throw new RuntimeException("Failed to call ChatModelApi", e);
        }
    }

}
