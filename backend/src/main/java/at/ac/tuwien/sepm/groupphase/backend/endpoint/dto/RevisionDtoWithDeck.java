package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import lombok.Data;

@Data
public class RevisionDtoWithDeck extends RevisionDto {
    private DeckSimpleDto deck;
}
