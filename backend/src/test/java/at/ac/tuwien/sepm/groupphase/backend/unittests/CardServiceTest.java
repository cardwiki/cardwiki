package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CardServiceTest extends TestDataGenerator {
    @Autowired
    private CardService cardService;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private DeckService deckService;

    private static final Long DECK_ID = 0L;

    @Test
    public void givenDeckAndUserExist_whenAddCardToDeck_thenReturnCard() {
        Revision revision = getUnconnectedSampleRevision();
        revision.setRevisionEdit(getUnconnectedSampleRevisionEdit());
        User user = getUnconnectedSampleUser();
        Deck deck = getUnconnectedSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(DECK_ID)).thenReturn(deck);
        when(cardRepository.saveAndFlush(any(Card.class))).then(returnsFirstArg());
        when(cardRepository.save(any(Card.class))).then(returnsFirstArg());

        Card returnedCard = cardService.addCardToDeck(DECK_ID, revision);

        verify(deckService).findOneOrThrow(DECK_ID);
        assertNotNull(returnedCard.getLatestRevision(), "Saves card with LatestRevision");
        assertAll(
            () -> assertEquals(deck, returnedCard.getDeck(), "Saves card with provided deck"),
            () -> assertEquals(user, returnedCard.getLatestRevision().getCreatedBy(), "Saves card with provided user")
        );
        Revision returnedRevision = returnedCard.getLatestRevision();
        assertNotNull(returnedRevision, "Saves card with LatestRevision");
        assertAll(
            () -> assertEquals(revision.getMessage(), returnedRevision.getMessage(), "Saves card with revision message"),
            () -> assertEquals(revision.getRevisionEdit(), returnedRevision.getRevisionEdit(), "Saves card with content")
        );
    }

    @Test
    public void givenDeckAndUserExist_whenAddCardToDeckWithoutMessage_thenReturnCardWithDefaultMessage() {
        Revision revision = getUnconnectedSampleRevision();
        revision.setRevisionEdit(getUnconnectedSampleRevisionEdit());
        revision.setMessage(null);
        User user = getUnconnectedSampleUser();
        Deck deck = getUnconnectedSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(DECK_ID)).thenReturn(deck);
        when(cardRepository.saveAndFlush(any(Card.class))).then(returnsFirstArg());
        when(cardRepository.save(any(Card.class))).then(returnsFirstArg());

        Card returnedCard = cardService.addCardToDeck(DECK_ID, revision);

        assertNotNull(returnedCard.getLatestRevision().getMessage(), "Saves card with default revision message");
    }

    @Test
    public void givenDeckExists_whenAddCardToDeck_thenThrowUserNotFoundException() {
        Revision revision = getUnconnectedSampleRevision();
        revision.setRevisionEdit(getUnconnectedSampleRevisionEdit());
        Deck deck = getUnconnectedSampleDeck();
        when(userService.loadCurrentUserOrThrow()).thenThrow(UserNotFoundException.class);
        when(deckService.findOneOrThrow(DECK_ID)).thenReturn(deck);

        assertThrows(UserNotFoundException.class, () -> cardService.addCardToDeck(DECK_ID, revision));
    }

    @Test
    public void givenUserExists_whenAddCardToDeck_thenThrowUserNotFoundException() {
        Revision revision = getUnconnectedSampleRevision();
        revision.setRevisionEdit(getUnconnectedSampleRevisionEdit());
        User user = getUnconnectedSampleUser();
        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(DECK_ID)).thenThrow(DeckNotFoundException.class);

        assertThrows(DeckNotFoundException.class, () -> cardService.addCardToDeck(DECK_ID, revision));
    }
}
