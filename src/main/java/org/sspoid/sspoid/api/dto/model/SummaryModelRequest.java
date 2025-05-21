package org.sspoid.sspoid.api.dto.model;

import org.sspoid.sspoid.db.chatmassage.SenderType;
import org.sspoid.sspoid.db.chatsession.SkinType;

public record SummaryModelRequest (
        SenderType sender,
        SkinType skinType,
        String message
){
}
