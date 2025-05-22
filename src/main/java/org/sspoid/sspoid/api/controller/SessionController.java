package org.sspoid.sspoid.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.sspoid.sspoid.api.dto.ChatSessionResponse;
import org.sspoid.sspoid.api.service.ChatBotService;
import org.sspoid.sspoid.common.resolver.CurrentUser;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SessionController {

    private final ChatBotService chatBotService;

    // 1. 세션 생성
    @PostMapping("/api/chat/sessions")
    public ResponseEntity<ChatSessionResponse> createSessions(@CurrentUser Long currentUserId) {
        return ResponseEntity.ok(chatBotService.createSession(currentUserId));
    }

    //2. 세션 제목 수정
    @PatchMapping("/api/chat/sessions/{id}/title")
    public ResponseEntity<ChatSessionResponse> updateSessionTitle(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @CurrentUser Long currentUserId
    ) throws AccessDeniedException {
        String newTitle = request.get("title");
        return ResponseEntity.ok(chatBotService.updateTitle(id, newTitle, currentUserId));
    }

    //세션 삭제
    @DeleteMapping("/api/chat/sessions/{id}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long id,
            @CurrentUser Long currentUserId
    ) throws AccessDeniedException {
        chatBotService.deleteSession(id, currentUserId);
        return ResponseEntity.ok().build();
    }

    // 5. 세션 목록 조회 (필터 가능)
    @GetMapping("/api/chat/sessions")
    public ResponseEntity<List<ChatSessionResponse>> getSessions() {
        return ResponseEntity.ok(chatBotService.getSessions());
    }
}
