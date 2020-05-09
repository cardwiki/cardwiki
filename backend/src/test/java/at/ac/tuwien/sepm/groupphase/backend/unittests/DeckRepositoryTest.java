package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class DeckRepositoryTest implements TestData {
    @Autowired
    private DeckRepository deckRepository;

    @Test
    public void givenDecks_whenFindByName_thenFindDecksContainingName() {
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck1.setName("testNameOne");
        deck2.setName("different");

        deck1.setCreatedAt(new Date());
        deck2.setCreatedAt(new Date());
        deck1.setUpdatedAt(new Date());
        deck2.setUpdatedAt(new Date());

        deckRepository.save(deck1);
        deckRepository.save(deck2);

        assertAll(
            () -> assertEquals(1,
                deckRepository.findByNameContainingIgnoreCase("NAME", PageRequest.of(0, 10)).size()),
            () -> assertTrue(
                deckRepository.findByNameContainingIgnoreCase("404", PageRequest.of(0, 10)).isEmpty()
            )
        );
    }
}
