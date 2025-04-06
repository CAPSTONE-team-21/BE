package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatmassage.ChatMessage;
import org.sspoid.sspoid.db.chatmassage.SenderType;
import org.sspoid.sspoid.db.chatsession.SkinType;

import java.util.List;

public record ChatMessageResponse (
        SenderType sender,
        List<SkinType> skinTypes,
        String message
){
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getSender(),
                message.getSkinTypes(),
                message.getMessage()
        );
    }
}
