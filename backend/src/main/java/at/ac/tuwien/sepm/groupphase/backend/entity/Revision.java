package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.parameters.P;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@Table(name = "revisions")
public abstract class Revision {
    public static final int MAX_MESSAGE_SIZE = 150;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="card_id", nullable=false, updatable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="created_by", nullable = false, updatable = false)
    private User createdBy;

    @Size(max = MAX_MESSAGE_SIZE)
    @NotBlank
    @Column(nullable = false, length = MAX_MESSAGE_SIZE, updatable = false)
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false)
    private String type;

    @PreRemove
    private void dismissContainers() {
        // Necessary to sync card in same session if revision is directly deleted
        card.dismissRevision(this);
        card = null;
        createdBy.dismissRevision(this);
        createdBy = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Revision{" +
            "id=" + id +
            ", type=" + type +
            ", createdBy=" + createdBy +
            ", card=" + (card != null ? card.getId() : null) +
            ", message='" + message + "'" +
            '}';
    }
}