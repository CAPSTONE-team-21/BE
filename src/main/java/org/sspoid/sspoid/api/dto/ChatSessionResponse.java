package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatmassage.ChatMessage;
import org.sspoid.sspoid.db.chatsession.ChatSession;

public record ChatSessionResponse (
    Long sessionId,
    String title,
    boolean isBookmark
){
    public static ChatSessionResponse from(ChatSession chatSession) {
        return new ChatSessionResponse(
                chatSession.getId(),
                chatSession.getTitle(),
                chatSession.isBookmark()
        );
    }
}
