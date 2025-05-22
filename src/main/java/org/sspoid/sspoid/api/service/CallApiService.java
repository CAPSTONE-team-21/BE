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
import org.sspoid.sspoid.api.dto.model.ChatModelRequest;
import org.sspoid.sspoid.api.dto.model.SummaryModelRequest;
import org.sspoid.sspoid.api.dto.model.SummaryModelResponse;
import org.sspoid.sspoid.db.chatsession.SkinType;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CallApiService {

    private final WebClient webClient;

    @Value("${model.api.model1-url}")
    private String ChatModel1_URL;

    @Value("${model.api.model2-url}")
    private String ChatModel2_URL;

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

    public String callChatModelApi(String message, SkinType skinType) {
        try {

            ChatModelRequest request = new ChatModelRequest(message, skinType.name());
            System.out.println("📤 [모델 요청] SkinGroup: " + skinType.name() + " | Message: " + message);

            ChatModelResponse response = webClient.post()
                    .uri(ChatModel1_URL)
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

    public SummaryModelResponse callSummaryModelApi(List<SummaryModelRequest> requests) {
        try {
            System.out.println("📤 [모델 요청] 요약 요청 - 총 메시지 수: " + requests.size());

            SummaryModelResponse response = webClient.post()
                    .uri(ChatModel2_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .acceptCharset(StandardCharsets.UTF_8)
                    .bodyValue(requests)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),  // ✅ 직접 람다로 체크
                            clientResponse -> clientResponse.bodyToMono(String.class).map(errorBody -> {
                                return new RuntimeException("❌ [모델 응답 오류] Status: " + clientResponse.statusCode() + " | Body: " + errorBody);
                            })
                    )
                    .bodyToMono(SummaryModelResponse.class)
                    .doOnNext(res -> System.out.println("📥 [모델 응답 수신 완료] 응답 메시지 길이: " + res.summary().length()))
                    .block();

            if (response == null || response.summary() == null) {
                System.err.println("⚠️ [모델 응답 없음 또는 null] message=null");
                throw new RuntimeException("모델 응답이 null입니다");
            }

            return response;
        }
        catch (Exception e) {
            System.err.println("🔥 [모델 API 호출 실패] 에러: " + e.getMessage());
            throw new RuntimeException("Failed to call ChatModelApi", e);
        }
    }

}
