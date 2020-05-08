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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "latest_revision", referencedColumnName = "id")
    private Revision latestRevision;

    @OneToMany(mappedBy="card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Revision> revisions = new HashSet<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

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

    @Override
    public String toString() {
        return "Card{" +
            "id=" + id +
            ", latestRevision=" + latestRevision +
            ", revisions=" + revisions +
            '}';
    }
}