package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import org.mapstruct.Mapper;

@Mapper
public interface DeckMapper {
    DeckDto deckToDeckDto(Deck deck);
    Deck deckDtoToDeck(DeckDto deckDto);
}
