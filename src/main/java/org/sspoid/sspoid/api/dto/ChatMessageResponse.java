package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatmassage.ChatMessage;
import org.sspoid.sspoid.db.chatmassage.SenderType;
import org.sspoid.sspoid.db.chatsession.SkinType;

public record ChatMessageResponse(
        SenderType sender,
        SkinType skinType,
        String message
) {
    public static ChatMessageResponse from(ChatMessage message, SkinType skinType) {
        return new ChatMessageResponse(
                message.getSender(),
                skinType,
                message.getMessage()
        );
    }
}