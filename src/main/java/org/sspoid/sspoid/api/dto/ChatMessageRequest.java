package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatsession.SkinType;

public record ChatMessageRequest ( //수정
        Long sessionId,
        String message,
        SkinType skinType
){
}
