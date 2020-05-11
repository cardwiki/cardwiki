package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deck_category")
public class DeckCategory {
    @EmbeddedId
    private DeckCategoryId id;

    @ManyToOne(optional = false)
    @MapsId("deckId")
    private Deck deck;

    //@ManyToOne(optional = false)
    //@MapsId("categoryId")
    //private Category category;

    //@ManyToOne(optional = false)
    //@JoinColumn(name = "created_by", nullable = false)
    //private ApplicationUser createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DeckCategoryId getId() {
        return id;
    }

    public void setId(DeckCategoryId id) {
        this.id = id;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    //public Category getCategory() {
    //    return category;
    //}

    //public void setCategory(Category category) {
    //    this.category = category;
    //}

    //public ApplicationUser getCreatedBy() {
    //    return createdBy;
    //}

    //public void setCreatedBy(ApplicationUser createdBy) {
    //    this.createdBy = createdBy;
    //}

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "DeckCategory{" +
            "id=" + id +
            //", createdBy=" + createdBy +
            ", createdAt=" + createdAt +
            '}';
    }
}
