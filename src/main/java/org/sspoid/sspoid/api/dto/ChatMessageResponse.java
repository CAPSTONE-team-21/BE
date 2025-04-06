package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatsession.SkinType;

import java.util.Map;

public record ChatMessageResponse(
        Map<SkinType, String> response
) {
    public static ChatMessageResponse from(Map<SkinType, String> responseMap) {
        return new ChatMessageResponse(responseMap);
    }
}