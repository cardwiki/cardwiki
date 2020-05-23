package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Progress {
    @EmbeddedId
    private Id id;

    private Long factor;

    private LocalDateTime due;

    @Embeddable
    public static class Id implements Serializable {
        @OneToOne
        private User user;

        @OneToOne
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
