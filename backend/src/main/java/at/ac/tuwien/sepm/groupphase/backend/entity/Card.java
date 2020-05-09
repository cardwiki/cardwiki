package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by") // TODO: Consider adding DELETED_USER and make it not nullable
    private ApplicationUser createdBy;

    @PreRemove
    private void dismissContainers() {
        deck.dismissCard(this);
        deck = null;
        createdBy.dismissCard(this);
        createdBy = null;
    }

    @PrePersist
    @PreUpdate
    private void syncRevisions() {
        if (latestRevision != null)
            revisions.add(latestRevision);
    }

    public void dismissRevision(Revision revision) {
        if (!revisions.remove(revision))
            throw new NoSuchElementException("Tried to dismiss revision which is not yet associated with card");

        if (latestRevision == revision)
            latestRevision = revisions.stream().max(Comparator.comparing(Revision::getCreatedAt)).orElse(null);
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ApplicationUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ApplicationUser createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Card{" +
            "id=" + id +
            ", createdBy=" + createdBy +
            ", latestRevision=" + latestRevision +
            ", revisions=" + revisions +
            '}';
    }
}