package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatsession.SkinType;

import java.util.Map;

public record ChatMessageSendResponse(
        Map<SkinType, String> response
) {
    public static ChatMessageSendResponse from(Map<SkinType, String> responseMap) {
        return new ChatMessageSendResponse(responseMap);
    }


}