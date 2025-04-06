package org.sspoid.sspoid.api.service;

import org.springframework.stereotype.Component;
import org.sspoid.sspoid.db.chatsession.SkinType;

@Component
public class ChatPromptBuilder {

    public String buildPrompt(String message, SkinType skinType) {
        return switch (skinType){
            case DRY -> "건성 피부 기준으로 답변해줘: " + message;
            case OILY -> "지성 피부 기준으로 답변해줘: " + message;
            case SENSITIVE -> "민감성 피부 기준으로 답변해줘: " + message;
            case COMBINED-> "복합성 피부 기준으로 답변해줘: " + message;
        };
    }
}
