package org.sspoid.sspoid.db.chatmassage;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sspoid.sspoid.db.BaseEntity;
import org.sspoid.sspoid.db.chatsession.ChatSession;
import org.sspoid.sspoid.db.chatsession.SkinType;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_session_id", nullable = false)
    private ChatSession chatSession;

    @Column(length = 150)
    @Enumerated(EnumType.STRING)
    private SenderType sender;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "skin_types",
            joinColumns = @JoinColumn(name = "chat_message_id")
    )
    @Enumerated(EnumType.STRING)
    private List<SkinType> skinTypes;

    @Lob
    @Column(name = "message", nullable = false, columnDefinition = "LONGTEXT")
    private String message;
}
