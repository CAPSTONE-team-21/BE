package org.sspoid.sspoid.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sspoid.sspoid.api.dto.ChatMessageRequest;
import org.sspoid.sspoid.api.dto.ChatMessageResponse;
import org.sspoid.sspoid.api.dto.ChatSessionResponse;
import org.sspoid.sspoid.api.dto.ChatSummaryResponse;
import org.sspoid.sspoid.api.dto.model.SummaryModelRequest;
import org.sspoid.sspoid.api.dto.model.SummaryModelResponse;
import org.sspoid.sspoid.db.chatmassage.ChatMessage;
import org.sspoid.sspoid.db.chatmassage.ChatMessageRepository;
import org.sspoid.sspoid.db.chatmassage.SenderType;
import org.sspoid.sspoid.db.chatsession.ChatSession;
import org.sspoid.sspoid.db.chatsession.ChatSessionRepository;
import org.sspoid.sspoid.db.chatsession.SkinGroup;
import org.sspoid.sspoid.db.chatsession.SkinType;
import org.sspoid.sspoid.db.user.User;
import org.sspoid.sspoid.db.user.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatBotService {

    private static final String DEFAULT_SESSION_TITLE = "제목 없음";
    private static final List<SkinGroup> DEFAULT_SKIN_TYPES = Arrays.asList(SkinGroup.values());

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private final ChatPromptBuilder promptBuilder;
    private final CallApiService callApiService;

    // 1. 세션 생성
    @Transactional
    public ChatSessionResponse createSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatSession chatSession = ChatSession.builder()
                .user(user)
                .title(DEFAULT_SESSION_TITLE)
                .isBookmark(false)
                .build();
        chatSessionRepository.save(chatSession);

        log.info("새로운 세션 생성 완료 - Session ID: {}", chatSession.getId());
        return ChatSessionResponse.from(chatSession);

    }

    // 2. 세션 제목 수정
    @Transactional
    public ChatSessionResponse updateTitle(Long id, String newTitle, Long currentUserId) throws AccessDeniedException {

        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("세션 제목 수정 실패 - 존재하지 않는 Session ID: {}", id);
                    return new RuntimeException("Session not found");
                });

        if (!session.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("해당 세션에 대한 권한이 없습니다.");
        }

        session.updateTitle(newTitle);

        log.info("세션 제목 수정 완료 - Session ID: {}, New Title: {}", id, newTitle);
        return ChatSessionResponse.from(session);
    }

    @Transactional
    public void deleteSession(Long id, Long currentUserId) throws AccessDeniedException {
        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("세션 삭제 실패 - 존재하지 않는 Session ID: {}", id);
                    return new RuntimeException("Session not found");
                });

        if (!session.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("해당 세션에 대한 권한이 없습니다.");
        }

        chatMessageRepository.deleteByChatSessionId(id);
        chatSessionRepository.delete(session);

        log.info("세션 삭제 완료 - Session ID: {}", id);
    }

    // 3. 전체 세션 리스트 조회
    public List<ChatSessionResponse> getSessions(Long currentUserId) throws AccessDeniedException {
        List<ChatSession> sessions = chatSessionRepository.findAll();

        List<ChatSession> authorizedSessions = sessions.stream()        // 현재 사용자에게 속한 세션만 필터링
                .filter(session -> session.getUser().getId().equals(currentUserId))
                .toList();

        log.info("사용자 {}의 세션 조회 완료 - 조회된 세션 수: {}", currentUserId, authorizedSessions.size());

        return authorizedSessions.stream()
                .map(ChatSessionResponse::from)
                .toList();
    }

    // 4. 메시지 전송 (유저 메시지 + 챗봇 응답 저장)
    @Transactional
    public List<ChatMessageResponse> sendMessage(Long id, ChatMessageRequest request, Long currentUserId) throws AccessDeniedException {

        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        if (!session.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("해당 세션에 대한 권한이 없습니다.");
        }

        List<SkinGroup> skinGroups = (request.skinTypes() == null || request.skinTypes().isEmpty())
                ? DEFAULT_SKIN_TYPES : request.skinTypes();

        List<SkinType> skinTypes = skinGroups.stream()
                .flatMap(group -> SkinType.fromSkinGroup(group).stream())
                .collect(Collectors.toList());

        log.info("🤍 스킨 타입: {}", skinTypes);

        //1. 메세지 전송
        ChatMessage userMessage = ChatMessage.builder()
                .chatSession(session)
                .sender(SenderType.USER)
                .skinTypes(skinTypes)
                .message(request.message())
                .build();
        chatMessageRepository.save(userMessage);
        log.info("사용자 메시지 저장 완료 - Session ID: {}, Message: {}", id, request.message());


        // 2. 각 skinType 별로 AI 응답 생성 + 저장 + DTO 매핑
        List<ChatMessageResponse> botResponses = skinTypes.stream().map(skinType -> {
            String prompt = promptBuilder.buildPrompt(request.message(), skinType.getSkinGroup());
            log.info("🔍 Sending request to Model API - SkinType: {}, Prompt: {}", skinType, prompt);

            String aiResponse = callApiService.callChatModelApi(prompt, skinType);

            // BOT 메시지 저장 - skinType은 단일로만 저장
            ChatMessage aiMessage = ChatMessage.builder()
                    .chatSession(session)
                    .sender(SenderType.BOT)
                    .skinTypes(List.of(skinType)) // 단일 스킨타입만 저장
                    .message(aiResponse)
                    .build();

            chatMessageRepository.save(aiMessage);

            log.info("BOT 응답 저장 완료 - Session ID: {}, SkinType: {}, Response Length: {}", id, skinType, aiResponse.length());

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
    public List<ChatMessageResponse> getMessagesBySessionId(Long id, Long currentUserId) throws AccessDeniedException {

        ChatSession session = chatSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        if (!session.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("해당 세션에 대한 권한이 없습니다.");
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatSessionId(id);

        log.info("세션 메시지 조회 완료 - Session ID: {}, 메시지 수: {}", id, messages.size());

        return messages.stream()
                .flatMap(message -> message.getSkinTypes().stream()
                        .map(skinType -> ChatMessageResponse.from(message, skinType)))
                .toList();
    }

    // 6. 요약 생성
    @Transactional
    public ChatSummaryResponse getSummary(Long sessionId, Long currentUserId) throws AccessDeniedException {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        log.info("올바른 세션 형식입니다! Session ID: {}", sessionId);

        if (!session.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("해당 세션에 대한 권한이 없습니다.");
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatSessionId(sessionId);

        List<SummaryModelRequest> requests = messages.stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(msg -> new SummaryModelRequest(
                        msg.getSender(),
                        msg.getSkinTypes().get(0),
                        msg.getMessage()
                ))
                .toList();

        SummaryModelResponse summary = callApiService.callSummaryModelApi(requests);

        return new ChatSummaryResponse(summary.summarizedMessage());
    }
}
