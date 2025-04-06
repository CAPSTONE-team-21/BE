package org.sspoid.sspoid.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sspoid.sspoid.api.dto.ChatMessageResponse;
import org.sspoid.sspoid.api.dto.ChatSessionRequest;
import org.sspoid.sspoid.api.dto.ChatSummaryResponse;
import org.sspoid.sspoid.api.dto.ChatSessionResponse;
import org.sspoid.sspoid.db.chatmassage.ChatMessage;
import org.sspoid.sspoid.db.chatmassage.ChatMessageRepository;
import org.sspoid.sspoid.db.chatsession.ChatSession;
import org.sspoid.sspoid.db.chatsession.ChatSessionRepository;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatBotService {

    private static final String DEFAULT_SESSION_TITLE = "제목 없음";

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 1. 세션 생성
    @Transactional
    public ChatSessionResponse createSession(ChatSessionRequest request) {
        ChatSession chatSession = ChatSession.builder()
                .title(DEFAULT_SESSION_TITLE)
                .isBookmark(false)
                .build();
        chatSessionRepository.save(chatSession);
        return ChatSessionResponse.from(chatSession);

    }

    // 2. 메시지 전송 (유저 메시지 + 챗봇 응답 저장)
    @Transactional
    public ChatMessageResponse sendMessage(ChatMessage message) {
        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ChatMessageResponse.from(savedMessage);
    }

    // 3. 세션 ID로 메시지 리스트 조회
    public List<ChatMessageResponse> getMessagesBySessionId(Long id) {
        List<ChatMessage> messages = chatMessageRepository.findByChatSession_Id(id);
        return messages.stream()
                .map(ChatMessageResponse::from)
                .toList();
    }

    // 4. 전체 세션 리스트 조회
    public List<ChatSessionResponse> getSessions() {
        List<ChatSession> sessions = chatSessionRepository.findAll();
        return sessions.stream()
                .map(ChatSessionResponse::from)
                .toList();
    }

    // 5. 요약 생성 (예시 구현, LLM 연동 필요)
    @Transactional
    public ChatSummaryResponse getSummary(Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        List<ChatMessage> messages = chatMessageRepository.findByChatSession_Id(sessionId);

        String summary = "임시 요약 메세지";

        return new ChatSummaryResponse(sessionId, summary); // 추후 수정
    }
}
