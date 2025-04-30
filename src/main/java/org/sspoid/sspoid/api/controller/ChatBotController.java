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

    //3. 메세지 전송
    @PostMapping("/api/chat/{id}/messages")
    public ResponseEntity<List<ChatMessageResponse>> sendMessage(
            @PathVariable Long id,
            @RequestBody ChatMessageRequest message
    ) {
        return ResponseEntity.ok(chatBotService.sendMessage(id, message));
    }

    // 4. 특정 세션에 저장되어있는 메시지 리스트 조회
    @GetMapping("/api/chat/sessions/{id}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(chatBotService.getMessagesBySessionId(id));
    }


//    // 5. 대화 요약
//    @PostMapping("/api/chat/sessions/{id}/summary")
//    public ResponseEntity<ChatSummaryResponse> getSummary(@PathVariable Long SessionId) {
//        return ResponseEntity.ok(chatBotService.getSummary());
//    }
}

