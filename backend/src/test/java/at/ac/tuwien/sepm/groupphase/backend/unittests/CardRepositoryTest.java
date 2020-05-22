package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class CardRepositoryTest extends TestDataGenerator {

    @Autowired
    private CardRepository cardRepository;

    @Test
    public void givenNothing_whenSaveCardWithoutDeck_thenThrowDataIntegrityViolationException() {
        Card card = new Card();
        assertThrows(DataIntegrityViolationException.class, () -> cardRepository.save(card));
    }

    @Test
    public void givenDeck_whenSaveCard_thenExistsById() {
        Deck deck = givenDeck();

        Card card = new Card();
        card.setDeck(deck);
        deck.getCards().add(card);
        cardRepository.save(card);

        assertTrue(cardRepository.existsById(card.getId()));
    }

    @Test
    public void givenDeckAndUser_whenSaveCardWithRevision_thenFindCardByIdHasLatestRevisionAndRevisionsIsNotEmpty() {
        Deck deck = givenDeck();
        User user = givenApplicationUser();

        Card card = new Card();
        card.setDeck(deck);
        deck.getCards().add(card);
        Revision revision = new Revision();
        revision.setMessage("some message");
        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setCreatedBy(user);
        cardRepository.save(card);

        assertEquals(revision, cardRepository.findById(card.getId()).orElseThrow().getLatestRevision());
        assertFalse(cardRepository.findById(card.getId()).orElseThrow().getRevisions().isEmpty());
    }

    @Test
    public void givenCardAndUser_whenAddRevision_thenFindCardByIdHasRevision() {
        Card card = givenCard();
        User user = givenApplicationUser();

        // When
        Revision revision = new Revision();
        revision.setMessage("some message");
        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setCreatedBy(user);
        cardRepository.saveAndFlush(card);

        // Then
        assertEquals(card.getLatestRevision(), cardRepository.findById(card.getId()).orElseThrow().getLatestRevision());
        assertFalse(cardRepository.findById(card.getId()).orElseThrow().getRevisions().isEmpty());
    }

    @Test
    public void givenCard_whenDeleteById_thenExistsByIdIsFalse() {
        Card card = givenCard();

        cardRepository.deleteById(card.getId());

        assertFalse(cardRepository.existsById(card.getId()));
    }

    @Test
    public void givenCard_whenDeleteById_thenDeckDoesNotContainCard() {
        Card card = givenCard();
        Deck deck = card.getDeck();

        assertTrue(deck.getCards().contains(card));
        cardRepository.deleteById(card.getId());

        assertFalse(deck.getCards().contains(card));
    }

    @Test
    public void givenCardAndRevisionEdit_whenDeleteById_thenNotExistsById() {
        RevisionEdit edit = givenRevisionEdit();
        Card card = edit.getRevision().getCard();

        // When
        cardRepository.deleteById(card.getId());
        // Then
        assertFalse(cardRepository.existsById(card.getId()));
    }

    @Test
    public void givenCardAndRevisionEdit_whenDeleteRevision_thenRevisionsIsEmpty() {
        RevisionEdit edit = givenRevisionEdit();
        Revision revision = edit.getRevision();
        Card card = revision.getCard();

        // When
        card.setLatestRevision(null);
        card.getRevisions().remove(revision);
        card = cardRepository.save(card);

        // Then
        assertTrue(cardRepository.findById(card.getId()).orElseThrow().getRevisions().isEmpty());
    }

    @Test
    public void givenDeck_whenFindByDeckId_thenFindCardsContainingDeck() {
        Card card = givenCard();
        Deck deck = card.getDeck();

        List<Card> cards = cardRepository.findCardsByDeck_Id(deck.getId());

        for (Card returnedCard: cards) {
            assertEquals(deck, returnedCard.getDeck());
        }
    }
}
