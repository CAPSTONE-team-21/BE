package org.sspoid.sspoid.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sspoid.sspoid.api.dto.ChatSessionResponse;
import org.sspoid.sspoid.db.chatsession.ChatSession;
import org.sspoid.sspoid.db.chatsession.ChatSessionRepository;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {

    private final ChatSessionRepository chatSessionRepository;

    @Transactional
    public void createBookmark(Long sessionId) {
            ChatSession session = chatSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션입니다: " + sessionId));
            session.setBookmark(true);
    }

    @Transactional
    public void deleteBookmark(Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 세션입니다: " + sessionId));
        session.setBookmark(false);
    }

    @Transactional
    public List<ChatSessionResponse> getBookmarks() {
        List<ChatSession> sessions = chatSessionRepository.findByIsBookmarkTrue();
        return sessions.stream()
                .map(ChatSessionResponse::from)
                .toList();
    }
}
