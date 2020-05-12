package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class DeckRepositoryTest extends TestDataGenerator {
    @Autowired
    private DeckRepository deckRepository;

    @Test
    public void givenDeck_whenFindByName_thenFindDeckContainingName() {
        Deck deck = givenDeck();
        PageRequest paging = PageRequest.of(0, 10);

        deckRepository.save(deck);

        assertAll(
            () -> assertEquals(1,
                deckRepository.findByNameContainingIgnoreCase(DECK_NAME, paging).size()
            ),
            () -> assertTrue(
                deckRepository.findByNameContainingIgnoreCase("404", paging).isEmpty()
            ),
            () -> assertEquals(1,
                deckRepository.findByNameContainingIgnoreCase(DECK_NAME.substring(1,3), paging).size()
            )
        );
    }

    @Test
    public void givenNothing_whenSaveDeck_thenThrowDataIntegrityViolation() {
        Deck deck = new Deck();
        assertThrows(DataIntegrityViolationException.class, () -> deckRepository.save(deck));
    }

    @Test
    public void givenUser_whenSaveDeck_thenThrowDataIntegrityViolation() {
        Deck deck = new Deck();
        deck.setCreatedBy(givenApplicationUser());
        assertThrows(DataIntegrityViolationException.class, () -> deckRepository.save(deck));
    }

    @Test
    public void givenName_whenSaveDeck_thenThrowDataIntegrityViolation() {
        Deck deck = new Deck();
        deck.setName("Test Name");
        assertThrows(DataIntegrityViolationException.class, () -> deckRepository.save(deck));
    }
}
