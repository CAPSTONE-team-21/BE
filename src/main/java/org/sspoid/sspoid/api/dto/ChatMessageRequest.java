package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatsession.SkinType;

import java.util.List;

public record ChatMessageRequest(
        String message,
        List<SkinType> skinTypes
) {}