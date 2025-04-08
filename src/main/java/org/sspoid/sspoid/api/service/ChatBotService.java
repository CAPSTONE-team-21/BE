package org.sspoid.sspoid.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sspoid.sspoid.api.dto.ChatMessageRequest;
import org.sspoid.sspoid.api.dto.ChatMessageResponse;
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

import static java.util.stream.Collectors.toList;

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

    // 3. 전체 세션 리스트 조회
    public List<ChatSessionResponse> getSessions() {
        List<ChatSession> sessions = chatSessionRepository.findAll();
        return sessions.stream()
                .map(ChatSessionResponse::from)
                .toList();
    }

    // 4. 메시지 전송 (유저 메시지 + 챗봇 응답 저장)
    @Transactional
    public List<ChatMessageResponse> sendMessage(ChatMessageRequest request) {

        List<SkinType> skinTypes = (request.skinTypes() == null || request.skinTypes().isEmpty())
                ? DEFAULT_SKIN_TYPES : request.skinTypes();

        //1. 메세지 전송
        ChatMessage userMessage = ChatMessage.builder()
                .chatSessionId(request.chatSessionId())
                .sender(SenderType.USER)
                .skinTypes(skinTypes)
                .message(request.message())
                .build();
        chatMessageRepository.save(userMessage);

        // 2. 각 skinType 별로 AI 응답 생성 + 저장 + DTO 매핑
        List<ChatMessageResponse> botResponses = skinTypes.stream().map(skinType -> {
            String prompt = promptBuilder.buildPrompt(request.message(), skinType);
            String aiResponse = callModel.generateResponse(prompt);

            // BOT 메시지 저장 - skinType은 단일로만 저장
            ChatMessage aiMessage = ChatMessage.builder()
                    .chatSessionId(request.chatSessionId())
                    .sender(SenderType.BOT)
                    .skinTypes(List.of(skinType)) // 단일 스킨타입만 저장
                    .message(aiResponse)
                    .build();
            chatMessageRepository.save(aiMessage);

            // DTO로 매핑
            return new ChatMessageResponse(
                    SenderType.BOT,
                    skinType,
                    aiResponse
            );
        }).toList();

        return botResponses;
    }

    // 5. 특정 세션에 저장되어있는 메시지 리스트 조회
    public List<ChatMessageResponse> getMessagesBySessionId(Long id) {
        List<ChatMessage> messages = chatMessageRepository.findByChatSessionId(id);

        return messages.stream()
                .flatMap(message -> message.getSkinTypes().stream()
                        .map(skinType -> ChatMessageResponse.from(message, skinType)))
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
