package org.sspoid.sspoid.db.chatsession;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sspoid.sspoid.db.BaseEntity;
import org.sspoid.sspoid.db.user.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_sessions")
public class ChatSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") //, nullable = false
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "mode")
    @Enumerated(EnumType.STRING)
    private ChatSessionMode mode;

    @Column(name = "is_bookmark", nullable = false)
    private boolean isBookmark;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void setBookmark(boolean isBookmark) {
        this.isBookmark = isBookmark;
    }
}
