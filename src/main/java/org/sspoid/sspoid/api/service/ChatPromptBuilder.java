package org.sspoid.sspoid.api.service;

import org.springframework.stereotype.Component;
import org.sspoid.sspoid.db.chatsession.SkinGroup;

@Component
public class ChatPromptBuilder {

    public String buildPrompt(String message, SkinGroup skinGroup) {
        return switch (skinGroup){
            case DRY -> message;
            case OILY -> message;
            case SENSITIVE -> message;
            case COMBINATION -> message;
        };
    }
}
