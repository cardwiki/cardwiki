package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.google.common.base.Objects;

import java.util.List;

public class CardDetailsDto {

    private Long id;
    private DeckSimpleDto deck;
    private List<RevisionSimpleDto> revisions;
    private String textFront;
    private String textBack;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeckSimpleDto getDeck() {
        return deck;
    }

    public void setDeck(DeckSimpleDto deck) {
        this.deck = deck;
    }

    public List<RevisionSimpleDto> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<RevisionSimpleDto> revisions) {
        this.revisions = revisions;
    }

    public String getTextFront() {
        return textFront;
    }

    public void setTextFront(String textFront) {
        this.textFront = textFront;
    }

    public String getTextBack() {
        return textBack;
    }

    public void setTextBack(String textBack) {
        this.textBack = textBack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardDetailsDto that = (CardDetailsDto) o;
        return Objects.equal(id, that.id) &&
            Objects.equal(deck, that.deck) &&
            Objects.equal(revisions, that.revisions) &&
            Objects.equal(textFront, that.textFront) &&
            Objects.equal(textBack, that.textBack);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, deck, revisions, textFront, textBack);
    }

    @Override
    public String toString() {
        return "CardDetailsDto{" +
            "id=" + id +
            ", deck=" + deck +
            ", revisions=" + revisions +
            ", textFront='" + textFront + '\'' +
            ", textBack='" + textBack + '\'' +
            '}';
    }
}
