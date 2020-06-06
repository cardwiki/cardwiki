package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public void givenDeck_whenCreateDeckCopy_thenCopyDeck() {
        Card card = givenCard();
        Deck deck = card.getDeck();
        User user = givenApplicationUser();
        Deck deckCopy = getSampleDeck();

        Deck returnedDeck = deckRepository.createDeckCopy(deck.getId(), user, deckCopy);

        assertAll(
            () -> assertNotEquals(returnedDeck.getId(), deck.getId()),
            () -> assertEquals(returnedDeck.getCreatedBy(), user),
            () -> assertEquals(returnedDeck.getName(), deckCopy.getName())
        );
    }
}
