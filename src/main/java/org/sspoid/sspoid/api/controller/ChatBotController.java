package org.sspoid.sspoid.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.sspoid.sspoid.api.dto.ChatMessageResponse;
import org.sspoid.sspoid.api.dto.ChatSessionRequest;
import org.sspoid.sspoid.api.dto.ChatSessionResponse;
import org.sspoid.sspoid.api.dto.ChatSummaryResponse;
import org.sspoid.sspoid.api.service.ChatBotService;
import org.sspoid.sspoid.db.chatmassage.ChatMessage;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatBotController {

    private ChatBotService chatBotService;

    // 1. 세션 생성
    @PostMapping("/api/chat/sessions")
    public ResponseEntity<ChatSessionResponse> createSessions(@RequestBody ChatSessionRequest request) {
        return ResponseEntity.ok(chatBotService.createSession(request));
    }

    // 2. 메시지 전송 → 유저 메시지 저장 + 챗봇 응답 저장
    @PostMapping("/api/chat/messages")
    private ResponseEntity<ChatMessageResponse> sendMessage(@RequestBody ChatMessage message) {
        return ResponseEntity.ok(chatBotService.sendMessage(message));

    }
    // 3. 세션 메시지 리스트 조회
    @GetMapping("/api/chat/sessions/{id}/messages")
    private ResponseEntity<List<ChatMessageResponse>> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(chatBotService.getMessage(id));
    }

    // 4. 세션 목록 조회 (필터 가능)
    @GetMapping("/api/chat/sessions")
    private ResponseEntity<List<ChatSessionResponse>> getSessions() {
        return ResponseEntity.ok(chatBotService.getSessions());
    }

    // 5. 대화 요약
    @PostMapping("/api/chat/sessions/{id}/summary")
    private ResponseEntity<ChatSummaryResponse> getSummary(@PathVariable Long SessionId) {
        return ResponseEntity.ok(chatBotService.getSummary());
    }
}

