package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "revisions")
public class Revision {
    public static final int MAX_MESSAGE_SIZE = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="card_id", nullable=false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by") // TODO: Consider adding DELETED_USER and make it not nullable
    private User createdBy;

    @OneToOne(mappedBy = "revision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RevisionEdit revisionEdit;

    @Size(max = MAX_MESSAGE_SIZE)
    @Column(nullable = false, length = MAX_MESSAGE_SIZE, updatable = false)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Created at object creation to guarantee consistent business key (date, message) for equals and hashcode

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

    public RevisionEdit getRevisionEdit() {
        return revisionEdit;
    }

    public void setRevisionEdit(RevisionEdit revisionEdit) {
        this.revisionEdit = revisionEdit;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Revision)) return false;
        Revision revision = (Revision) o;
        return Objects.equals(createdAt, revision.createdAt) &&
            Objects.equals(message, revision.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, createdAt);
    }

    @Override
    public String toString() {
        return "Revision{" +
            "id=" + id +
            ", createdBy=" + createdBy +
            ", card=" + (card != null ? card.getId() : null) +
            ", message='" + message + "'" +
            ", revisionEdit=" + revisionEdit.getId() +
            '}';
    }
}