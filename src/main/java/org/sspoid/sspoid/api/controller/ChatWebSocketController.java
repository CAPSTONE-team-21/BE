package org.sspoid.sspoid.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.sspoid.sspoid.api.dto.ChatMessageSendRequest;
import org.sspoid.sspoid.api.dto.ChatMessageSendResponse;
import org.sspoid.sspoid.api.service.ChatBotService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatBotService chatBotService;

    @MessageMapping("/chat/message") // /app/chat/message 로 메시지 수신
    @SendTo("/topic/chat")           // /topic/chat 로 응답 broadcast
    public ChatMessageSendResponse handleMessage(ChatMessageSendRequest messageRequest) {
        log.info("웹소켓 메시지 수신: {}", messageRequest.message());
        ChatMessageSendResponse response = chatBotService.sendMessage(messageRequest);

        return response;
    }
}
