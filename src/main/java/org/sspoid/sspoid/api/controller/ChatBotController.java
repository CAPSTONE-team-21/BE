package org.sspoid.sspoid.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.sspoid.sspoid.api.dto.ChatMessageRequest;
import org.sspoid.sspoid.api.dto.ChatMessageResponse;
import org.sspoid.sspoid.api.dto.ChatSessionResponse;
import org.sspoid.sspoid.api.service.ChatBotService;
import org.sspoid.sspoid.db.chatmassage.ChatMessage;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatBotService chatBotService;

    // 1. 세션 생성
    @PostMapping("/api/chat/sessions")
    public ResponseEntity<ChatSessionResponse> createSessions() {
        return ResponseEntity.ok(chatBotService.createSession());
    }

    //2. 세션 제목 수정
    @PatchMapping("/api/chat/sessions/{id}/title")
    public ResponseEntity<ChatSessionResponse> updateSessionTitle(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String newTitle = request.get("title");
        return ResponseEntity.ok(chatBotService.updateTitle(id, newTitle));
    }

    //3. 메세지 전송
    @PostMapping("/api/chat/{id}/messages")
    private ResponseEntity<List<ChatMessageResponse>> sendMessage(
            @PathVariable Long id,
            @RequestBody ChatMessageRequest message
    ) {
        return ResponseEntity.ok(chatBotService.sendMessage(id, message));
    }

    // 4. 특정 세션에 저장되어있는 메시지 리스트 조회
    @GetMapping("/api/chat/sessions/{id}/messages")
    private ResponseEntity<List<ChatMessageResponse>> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(chatBotService.getMessagesBySessionId(id));
    }

    // 5. 세션 목록 조회 (필터 가능)
    @GetMapping("/api/chat/sessions")
    private ResponseEntity<List<ChatSessionResponse>> getSessions() {
        return ResponseEntity.ok(chatBotService.getSessions());
    }

//    // 5. 대화 요약
//    @PostMapping("/api/chat/sessions/{id}/summary")
//    private ResponseEntity<ChatSummaryResponse> getSummary(@PathVariable Long SessionId) {
//        return ResponseEntity.ok(chatBotService.getSummary());
//    }
}

