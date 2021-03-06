package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="deck_id", nullable=false)
    private Deck deck;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "latest_revision", referencedColumnName = "id")
    private Revision latestRevision;

    @OneToMany(mappedBy="card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Revision> revisions = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "id.card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Progress> progresses;

    @PrePersist
    @PreUpdate
    private void syncRevisions() {
        if (latestRevision != null)
            revisions.add(latestRevision);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Revision getLatestRevision() {
        return latestRevision;
    }

    public void setLatestRevision(Revision revision) {
        this.latestRevision = revision;
    }

    public Set<Revision> getRevisions() {
        return revisions;
    }

    public void setRevisions(Set<Revision> revisions) {
        this.revisions = revisions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Card{" +
            "id=" + id +
            ", latestRevision=" + latestRevision +
            ", revisions=" + revisions +
            '}';
    }
}