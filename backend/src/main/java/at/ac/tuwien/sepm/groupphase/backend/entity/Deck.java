package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "decks")
public class Deck {
    public static final int MAX_NAME_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = MAX_NAME_LENGTH)
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "deck")
    private Set<Card> cards = new HashSet<>();

    //@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "deck")
    //private Set<Comment> comments = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "decks")
    private Set<Category> categories = new HashSet<>();


    public void dismissCard(Card card) {
        if (!cards.remove(card))
            throw new NoSuchElementException("Tried to dismiss card which is not yet associated with deck");
    }

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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    //public Set<Comment> getComments() {
    //    return comments;
    //}

    //public void setComments(Set<Comment> comments) {
    //    this.comments = comments;
    //}


    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Deck{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", createdBy=" + createdBy +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
