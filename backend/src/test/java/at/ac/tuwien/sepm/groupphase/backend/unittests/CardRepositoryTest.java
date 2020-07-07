package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class CardRepositoryTest extends TestDataGenerator {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DeckRepository deckRepository;

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
        RevisionCreate revision = new RevisionCreate();
        revision.setMessage("some message");
        revision.setTextBack("foo");
        revision.setTextFront("foo");
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
        RevisionEdit revision = new RevisionEdit();
        revision.setMessage("some message");
        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setCreatedBy(user);
        revision.setTextBack("foo");
        revision.setTextFront("foo");
        cardRepository.saveAndFlush(card);

        // Then
        assertEquals(card.getLatestRevision(), cardRepository.findById(card.getId()).orElseThrow().getLatestRevision());
        assertFalse(cardRepository.findById(card.getId()).orElseThrow().getRevisions().isEmpty());
    }

    @Test
    public void givenCardAndRevisionEdit_whenDeleteRevision_thenRevisionsIsEmpty() {
        RevisionEdit revision = givenRevisionEdit();
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

    @Test
    public void givenDeck_whenFindCardsWithContentByDeckId_thenFindCardsWithContentOfDeck() {
        RevisionEdit revision = givenRevisionEdit();
        Card card = revision.getCard();
        Deck deck = card.getDeck();

        List<Card> cards = cardRepository.findCardsWithContentByDeck_Id(deck.getId()).collect(Collectors.toList());
        assertTrue(cards.contains(card));
        for (Card returnedCard: cards) {
            assertEquals(deck, returnedCard.getDeck());
        }

        User user = givenApplicationUser();
        RevisionDelete deleteRevision = new RevisionDelete();
        deleteRevision.setCard(card);
        deleteRevision.setMessage("Delete");
        card.setLatestRevision(deleteRevision);
        deleteRevision.setCreatedBy(user);
        cardRepository.saveAndFlush(card);

        cards = cardRepository.findCardsWithContentByDeck_Id(deck.getId()).collect(Collectors.toList());

        assertFalse(cards.contains(card));
    }

    @Test
    public void givenDeckWithFrontText_whenFilterExistingFrontTexts_thenReturnFrontText() {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.setMessage("Created");
        revisionCreate.setTextFront("Existing Front");
        revisionCreate.setTextBack("Random Back Side");
        agent.createCardIn(deck, revisionCreate);

        Set<String> filtered = cardRepository.filterExistingFrontTexts(deck.getId(), Collections.singletonList("Existing Front"));

        assertEquals(1, filtered.size(), "Filtered set found existing revision");
        assertTrue(filtered.contains("Existing Front"), "Filtered set includes existing revision");
    }

    @Test
    public void givenDeckWithDifferentFrontText_whenFilterExistingFrontTexts_thenReturnFrontText() {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.setMessage("Created");
        revisionCreate.setTextFront("Existing Front");
        revisionCreate.setTextBack("Random Back Side");
        agent.createCardIn(deck, revisionCreate);

        Set<String> filtered = cardRepository.filterExistingFrontTexts(deck.getId(), Collections.singletonList("Other Front"));

        assertTrue(filtered.isEmpty(), "Filtered set found no existing revisions");
    }
}
