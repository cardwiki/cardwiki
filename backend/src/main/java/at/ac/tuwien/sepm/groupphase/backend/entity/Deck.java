package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "decks")
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String name;

    //@ManyToOne(fetch = FetchType.LAZY, optional = false)
    //@JoinColumn(name = "created_by", nullable = false, updatable = false)
    //private ApplicationUser createdBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    //@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "deck")
    //private Set<Card> cards = new HashSet<>();

    //@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "deck")
    //private Set<Comment> comments = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "deck")
    private Set<DeckCategory> categories = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //public ApplicationUser getCreatedBy() {
    //    return createdBy;
    //}

    //public void setCreatedBy(ApplicationUser createdBy) {
    //    this.createdBy = createdBy;
    //}

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    //public Set<Card> getCards() {
    //    return cards;
    //}

    //public void setCards(Set<Card> cards) {
    //    this.cards = cards;
    //}

    //public Set<Comment> getComments() {
    //    return comments;
    //}

    //public void setComments(Set<Comment> comments) {
    //    this.comments = comments;
    //}

    public Set<DeckCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<DeckCategory> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Deck{" +
            "id=" + id +
            ", name='" + name + '\'' +
            //", createdBy=" + createdBy +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}