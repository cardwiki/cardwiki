package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface DeckMapper {
    @Named("deckToDeckDto")
    @Mapping(source = "createdBy.id", target = "createdBy")
    DeckDto deckToDeckDto(Deck deck);

    Deck deckInputDtoToDeck(DeckInputDto deckInputDto);

    DeckSimpleDto deckToDeckSimpleDto(Deck deck);

    Deck deckUpdateDtoToDeck(DeckUpdateDto deckUpdateDto);

    @Mapping(source = "", target = "id")
    Category idToCategory(Long id);
}
