package org.sspoid.sspoid.api.dto.model;

import org.sspoid.sspoid.db.chatmassage.SenderType;
import org.sspoid.sspoid.db.chatsession.SkinType;

import java.util.List;

public record SummaryModelRequest (
        SenderType sender,
        List<SkinType> skinType,
        String message
){
}
