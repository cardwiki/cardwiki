package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DeckMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DeckMappingTest extends TestDataGenerator {
    @Autowired
    private DeckMapper deckMapper;

    @Test
    public void givenNothing_whenMapDeckToDeckDto_thenDtoHasAllProperties() {
        Deck deck = givenDeck();
        DeckDto deckDto = deckMapper.deckToDeckDto(deck);
        assertAll(
            () -> assertEquals(deck.getId(), deckDto.getId()),
            () -> assertEquals(deck.getName(), deckDto.getName()),
            () -> assertEquals(deck.getCreatedBy().getId(), deckDto.getCreatedBy()),
            () -> assertEquals(deck.getCreatedAt(), deckDto.getCreatedAt()),
            () -> assertEquals(deck.getUpdatedAt(), deckDto.getUpdatedAt()),
            () -> assertEquals(
                deck.getCategories().stream().map(
                    (x) -> {
                        CategorySimpleDto dto = new CategorySimpleDto();
                        dto.setId(x.getId());
                        dto.setName(x.getName());
                        return dto;
                    }
                ).collect(Collectors.toList()),
                deckDto.getCategories()
            )
        );
    }

    @Test
    public void givenNothing_whenMapDeckInputDtoToDeck_thenDeckHasAllProperties() {
        String name = "Test Name";
        DeckInputDto deckInputDto = new DeckInputDto();
        deckInputDto.setName(name);
        Deck deck = deckMapper.deckInputDtoToDeck(deckInputDto);
        assertEquals(deckInputDto.getName(), deck.getName());
    }

    @Test
    public void givenDeckUpdateDto_whenMapToDeck_thenDeckHasAllProperties() {
        DeckUpdateDto deckUpdateDto = new DeckUpdateDto();
        deckUpdateDto.setName("Test Deck");

        Deck deck = deckMapper.deckUpdateDtoToDeck(deckUpdateDto);

        assertEquals(deckUpdateDto.getName(), deck.getName());
    }
}
