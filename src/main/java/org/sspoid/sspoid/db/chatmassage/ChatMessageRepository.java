package org.sspoid.sspoid.db.chatmassage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatSessionId(Long id);

    void deleteByChatSessionId(Long id);
}
