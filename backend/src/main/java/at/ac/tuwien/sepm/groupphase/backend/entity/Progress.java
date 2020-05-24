package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Progress {
    @EmbeddedId
    private Id id;

    private long factor;

    public static final String FKNAME_USER = "FK_PROGRESS_USER";
    public static final String FKNAME_CARD = "FK_PROGRESS_CARD";

    @NotNull
    private LocalDateTime due;

    public Progress(){
    }

    public Progress(Id id) {
        this.id = id;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public long getFactor() {
        return factor;
    }

    public void setFactor(long factor) {
        this.factor = factor;
    }

    public LocalDateTime getDue() {
        return due;
    }

    public void setDue(LocalDateTime due) {
        this.due = due;
    }

    @Embeddable
    public static class Id implements Serializable {
        @ManyToOne
        @JoinColumn(foreignKey = @ForeignKey(name = FKNAME_USER))
        private User user;

        @ManyToOne
        @JoinColumn(foreignKey = @ForeignKey(name = FKNAME_CARD))
        private Card card;

        public Id() {
        }

        public Id(User user, Card card) {
            this.user = user;
            this.card = card;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return user.equals(id.user) &&
                card.equals(id.card);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, card);
        }
    }
}
