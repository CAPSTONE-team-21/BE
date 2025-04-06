package org.sspoid.sspoid.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sspoid.sspoid.api.dto.ChatMessageResponse;
import org.sspoid.sspoid.api.dto.ChatMessageSendRequest;
import org.sspoid.sspoid.api.dto.ChatMessageSendResponse;
import org.sspoid.sspoid.api.dto.ChatSessionResponse;
import org.sspoid.sspoid.db.chatmassage.ChatMessage;
import org.sspoid.sspoid.db.chatmassage.ChatMessageRepository;
import org.sspoid.sspoid.db.chatmassage.SenderType;
import org.sspoid.sspoid.db.chatsession.ChatSession;
import org.sspoid.sspoid.db.chatsession.ChatSessionRepository;
import org.sspoid.sspoid.db.chatsession.SkinType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatBotService {

    private static final String DEFAULT_SESSION_TITLE = "제목 없음";
    private static final List<SkinType> DEFAULT_SKIN_TYPES = Arrays.asList(SkinType.values());

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final ChatPromptBuilder promptBuilder;
    private final CallModel callModel;

    // 1. 세션 생성
    @Transactional
    public ChatSessionResponse createSession() {
        ChatSession chatSession = ChatSession.builder()
                .title(DEFAULT_SESSION_TITLE)
                .isBookmark(false)
                .build();
        chatSessionRepository.save(chatSession);
        return ChatSessionResponse.from(chatSession);

    }

    // 2. 세션 제목 수정
    @Transactional
    public ChatSessionResponse updateTitle(Long id, String newTitle) {
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.updateTitle(newTitle);
        return ChatSessionResponse.from(session);
    }

    // 3. 메시지 전송 (유저 메시지 + 챗봇 응답 저장)
    @Transactional
    public ChatMessageSendResponse sendMessage(ChatMessageSendRequest request) {

        List<SkinType> skinTypes = (request.skinTypes() == null || request.skinTypes().isEmpty())
                ? DEFAULT_SKIN_TYPES
                : request.skinTypes();

        //메세지 전송
        ChatMessage userMessage = ChatMessage.builder()
                .chatSessionId(request.chatSessionId())
                .sender(SenderType.USER)
                .skinTypes(skinTypes)
                .message(request.message())
                .build();
        chatMessageRepository.save(userMessage);

        //skinType별 응답 생성
        Map<SkinType, String> responseMap = new HashMap<>();
        for (SkinType skinType : skinTypes) {
            String prompt = promptBuilder.buildPrompt(request.message(), skinType);
            String aiResponse = callModel.generateResponse(prompt); // 하나의 모델을 반복 호출

            ChatMessage aiMessage = ChatMessage.builder()
                    .chatSessionId(request.chatSessionId())
                    .sender(SenderType.BOT)
                    .skinTypes(skinTypes)
                    .message(aiResponse)
                    .build();
            chatMessageRepository.save(aiMessage);

            responseMap.put(skinType, aiResponse);        }

        return ChatMessageSendResponse.from(responseMap);
    }

    // 4. 세션 ID로 메시지 리스트 조회
    public List<ChatMessageResponse> getMessagesBySessionId(Long id) {
        List<ChatMessage> messages = chatMessageRepository.findByChatSessionId(id);
        return messages.stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    // 5. 전체 세션 리스트 조회
    public List<ChatSessionResponse> getSessions() {
        List<ChatSession> sessions = chatSessionRepository.findAll();
        return sessions.stream()
                .map(ChatSessionResponse::from)
                .toList();
    }

    // 6. 요약 생성
//    @Transactional
//    public ChatSummaryResponse getSummary(Long sessionId) {
//        ChatSession session = chatSessionRepository.findById(sessionId)
//                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
//
//        List<ChatMessage> messages = chatMessageRepository.findByChatSession_Id(sessionId);
//
//        String summary = "임시 요약 메세지";
//
//        return new ChatSummaryResponse(sessionId, summary); // 추후 수정
//    }

}
