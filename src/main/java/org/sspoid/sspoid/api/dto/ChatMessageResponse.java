package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatmassage.ChatMessage;
import org.sspoid.sspoid.db.chatmassage.SenderType;
import org.sspoid.sspoid.db.chatsession.SkinGroup;

public record ChatMessageResponse(
        SenderType sender,
        SkinGroup skinGroup,
        String message
) {
    public static ChatMessageResponse from(ChatMessage message, SkinGroup skinGroup) {
        return new ChatMessageResponse(
                message.getSender(),
                skinGroup,
                message.getMessage()
        );
    }
}