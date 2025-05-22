package org.sspoid.sspoid.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.sspoid.sspoid.api.dto.ChatMessageRequest;
import org.sspoid.sspoid.api.dto.ChatMessageResponse;
import org.sspoid.sspoid.api.dto.ChatSummaryResponse;
import org.sspoid.sspoid.api.service.ChatBotService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatBotService chatBotService;

    //3. 메세지 전송
    @PostMapping(value = "/api/chat/{id}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseBodyEmitter sendMessage(
            @PathVariable Long id,
            @RequestBody ChatMessageRequest message,
            @CurrentUser Long currentUserId
    ) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        ObjectMapper objectMapper = new ObjectMapper();

        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                emitter.send("[", MediaType.APPLICATION_JSON); // cloudflare timeout 우회를 위해 첫 바이트 먼저 전송

                List<ChatMessageResponse> responses = chatBotService.sendMessage(id, message, currentUserId);

                for (int i = 0; i < responses.size(); i++) {
                    String json = objectMapper.writeValueAsString(responses.get(i));
                    if (i > 0) emitter.send(",", MediaType.APPLICATION_JSON);
                    emitter.send(json, MediaType.APPLICATION_JSON);
                }

                emitter.send("]", MediaType.APPLICATION_JSON);

                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    // 4. 특정 세션에 저장되어있는 메시지 리스트 조회
    @GetMapping("/api/chat/sessions/{id}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessage(
            @PathVariable Long id,
            @CurrentUser Long currentUserId
    ) {
        try {
            return ResponseEntity.ok(chatBotService.getMessagesBySessionId(id, currentUserId));
        } catch (AccessDeniedException e) {
            log.warn("🚓 인가 실패: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }

    // 5. 대화 요약
    @GetMapping("/api/chat/sessions/{id}/summary")
    public ResponseEntity<ChatSummaryResponse> getSummary(
            @PathVariable Long id,
            @CurrentUser Long currentUserId) {
        try {
            return ResponseEntity.ok(chatBotService.getSummary(id, currentUserId));
        } catch (AccessDeniedException e) {
            log.warn("🚓 인가 실패: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }
}


