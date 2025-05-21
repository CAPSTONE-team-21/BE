package org.sspoid.sspoid.api.dto;

import org.sspoid.sspoid.db.chatsession.SkinGroup;

import java.util.List;

public record ChatMessageRequest(
        String message,
        List<SkinGroup> skinTypes
) {}