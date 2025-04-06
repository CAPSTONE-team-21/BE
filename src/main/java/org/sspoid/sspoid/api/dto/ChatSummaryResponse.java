package org.sspoid.sspoid.api.dto;

public record ChatSummaryResponse (
        Long sessionId,
        String summarizedMessage
){
}
