package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "progress")
public class Progress {
    public static final String FKNAME_USER = "FK_PROGRESS_USER";
    public static final String FKNAME_CARD = "FK_PROGRESS_CARD";

    @EmbeddedId
    private Id id;

    private int easinessFactor;

    // named ivl instead of interval because that's a reserved SQL keyword
    private int ivl;

    public enum Status {
        LEARNING,
        REVIEWING
    }

    @NotNull
    private Status status;

    @NotNull
    private LocalDateTime due;

    public Progress(){
    }

    public Progress(Id id) {
        this.id = id;
        easinessFactor = 250;
        status = Status.LEARNING;
        ivl = 1;
    }

    public int getInterval() {
        return ivl;
    }

    public void setInterval(int interval) {
        this.ivl = interval;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public int getEasinessFactor() {
        return easinessFactor;
    }

    public void setEasinessFactor(int easinessFactor) {
        this.easinessFactor = easinessFactor;
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
