package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DeckMapper {
    @Mapping(source = "createdBy.username", target = "createdBy")
    DeckDto deckToDeckDto(Deck deck);

    Deck deckInputDtoToDeck(DeckInputDto deckInputDto);
}
