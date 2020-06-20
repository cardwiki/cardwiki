package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
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

        deckRepository.save(deck);

        assertAll(
            () -> assertEquals(1,
                deckRepository.findByNameContainingIgnoreCase(deck.getName(), Pageable.unpaged()).size()
            ),
            () -> assertEquals(1,
                deckRepository.findByNameContainingIgnoreCase(deck.getName().substring(1,3), Pageable.unpaged()).size()
            )
        );
    }

    @Test
    public void givenDeck_whenFindByNameNotContained_thenEmptyList() {
        Deck deck = givenDeck();

        assertTrue(deckRepository.findByNameContainingIgnoreCase("NotAName", Pageable.unpaged()).isEmpty());
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

    @Test
    public void givenId_whenFindById_thenFindDeck() {
        Deck deck = givenDeck();

        Optional<Deck> returnedDeck = deckRepository.findById(deck.getId());

        returnedDeck.ifPresent(value -> assertEquals(deck, value));
    }

    @Test void givenNothing_whenFindById_thenResultIsNull() {
        assertTrue(deckRepository.findById(1L).isEmpty());
    }

    @Test
    public void givenNoDecks_whenDeleteById_thenThrowEmptyResultDataAccessException() {
        assertThrows(EmptyResultDataAccessException.class, () -> deckRepository.deleteById(1L));
    }

    @Test void givenUser_whenFindFavoredById_thenReturnEmpty() {
        Long userId = givenApplicationUser().getId();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Deck> result = deckRepository.findByFavoredById(userId, pageable);

        assertTrue(result.isEmpty());
    }

    @Test void givenFavorite_whenFindFavoredById_thenReturnFavorite() {
        Deck deck = givenFavorite();
        Long userId = deck.getCreatedBy().getId();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Deck> result = deckRepository.findByFavoredById(userId, pageable);

        assertEquals(1, result.getNumberOfElements());
        Deck resultDeck = result.iterator().next();
        assertNotNull(resultDeck);
        assertAll(
            () -> assertEquals(deck.getId(), resultDeck.getId()),
            () -> assertEquals(deck.getName(), resultDeck.getName())
        );
    }

    @Test void givenDeckAndUser_whenexistsByIdAndFavoredById_thenReturnFalse() {
        Long deckId = givenDeck().getId();
        Long userId = givenApplicationUser().getId();

        assertFalse(deckRepository.existsByIdAndFavoredById(deckId, userId));
    }

    @Test void givenFavorite_whenexistsByIdAndFavoredById_thenReturnTrue() {
        Deck deck = givenFavorite();
        Long userId = deck.getCreatedBy().getId();
        Long deckId = deck.getId();

        assertTrue(deckRepository.existsByIdAndFavoredById(deckId, userId));
    }

    @Test void givenNothing_whenexistsByIdAndFavoredById_thenThrow() {
        assertFalse(() -> deckRepository.existsByIdAndFavoredById(0L, 0L));
    }
}
