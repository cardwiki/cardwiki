package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ProgressRepositoryTest extends TestDataGenerator {
    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private CardRepository cardRepository;

    @Test
    public void givenNewCard_whenFindNext_thenReturnCard() {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);

        assertEquals(
            1,
            cardRepository.findNextCards(
                deck.getId(),
                user.getId(),
                false,
                Pageable.unpaged()
            ).size()
        );
    }

    @Test
    public void givenDueCard_whenFindNext_thenReturnCard() {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressDue(card, false);

        assertEquals(
            1,
            cardRepository.findNextCards(
                deck.getId(),
                user.getId(),
                false,
                Pageable.unpaged()
            ).size()
        );
    }

    @Test
    public void givenCardNotDue_whenFindNext_thenReturnEmptyList() {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressNotDue(card, false);

        assertEquals(
            0,
            cardRepository.findNextCards(
                deck.getId(),
                user.getId(),
                false,
                Pageable.unpaged()
            ).size()
        );
    }

    @Test
    public void givenDueReverseCard_whenFindNext_thenReturnCard() {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressDue(card, true);

        assertEquals(
            1,
            cardRepository.findNextCards(
                deck.getId(),
                user.getId(),
                true,
                Pageable.unpaged()
            ).size()
        );
    }

    @Test
    public void givenNotDueReverseCard_whenFindNextNormal_thenReturnSingleResult() {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressNotDue(card, true);

        assertEquals(
            1,
            cardRepository.findNextCards(
                deck.getId(),
                user.getId(),
                false,
                Pageable.unpaged()
            ).size()
        );
    }

    @Test
    public void givenDeletedCard_whenFindNext_thenReturnEmptyList() {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.deleteCard(card);

        assertEquals(
            0,
            cardRepository.findNextCards(
                deck.getId(),
                user.getId(),
                false,
                Pageable.unpaged()
            ).size()
        );
    }
}
