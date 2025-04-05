package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatmassage.ChatMessage;

public record ChatMessageResponse ( //수정
        String message
){
    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
                chatMessage.getMessage()
        );
    }
}
