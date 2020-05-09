package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.DeckCategoryId;
import org.mapstruct.Mapper;

@Mapper
public abstract class DeckMapper {
    public abstract DeckDto deckToDeckDto(Deck deck);

    public abstract Deck deckInputDtoToDeck(DeckInputDto deckInputDto);

    public Long test(DeckCategoryId deckCategoryId) {
        return deckCategoryId.getCategoryId();
    }
}
