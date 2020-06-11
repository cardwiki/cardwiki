package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DeckServiceTest extends TestDataGenerator {
    @MockBean
    private DeckRepository deckRepository;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private DeckService deckService;

    @Test
    public void givenNothing_whenFindOneNonexistent_thenThrowNotFoundException() {
        Long id = 1L;
        Mockito.when(deckRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> deckService.findOne(id));
    }

    @Test
    public void givenNothing_whenFindOneExistent_thenReturnDeck() {
        Long id = 1L;
        Deck deck = getSampleDeck();
        Mockito.when(deckRepository.findById(id)).thenReturn(Optional.of(deck));
        assertEquals(deck, deckService.findOne(id));
    }

    @Test
    public void givenNothing_whenFindOneArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> deckService.findOne(null));
    }

    @Test
    public void givenNothing_whenSearchByNameArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> deckService.searchByName(null, Pageable.unpaged()));
    }

    @Test
    public void givenNothing_whenSearchByNameNotExistent_thenReturnEmptyList() {
        Mockito.when(deckRepository.findByNameContainingIgnoreCase("", Pageable.unpaged()))
            .thenReturn(Collections.emptyList());
        assertTrue(deckService.searchByName("", Pageable.unpaged()).isEmpty());
    }

    @Test
    public void givenNothing_whenSearchByNameExistent_thenReturnDeck() {
        Deck deck = getSampleDeck();
        Mockito.when(deckRepository.findByNameContainingIgnoreCase(deck.getName(), Pageable.unpaged()))
            .thenReturn(Collections.singletonList(deck));
        assertTrue(deckService.searchByName(deck.getName(), Pageable.unpaged()).contains(deck));
    }

    @Test
    public void givenNothing_whenCreate_thenReturnDeckWithCorrectUser() {
        Deck deck = getSampleDeck();
        Deck simpleDeck = new Deck();
        simpleDeck.setName(deck.getName());

        Mockito.when(deckRepository.save(simpleDeck)).thenReturn(deck);
        Mockito.when(userService.loadCurrentUser()).thenReturn(deck.getCreatedBy());
        assertEquals(deck, deckService.create(simpleDeck));
    }

    @Test
    public void givenNothing_whenCreateNoCurrentUser_thenThrowIllegalState() {
        Deck deck = getSampleDeck();
        Deck simpleDeck = new Deck();
        simpleDeck.setName("Name");

        Mockito.when(deckRepository.save(simpleDeck)).thenReturn(deck);
        Mockito.when(userService.loadCurrentUser()).thenReturn(null);
        assertThrows(IllegalStateException.class, () -> deckService.create(simpleDeck));
    }

    @Test
    public void givenNothing_whenCreateArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> deckService.create(null));
    }

    @Test
    public void givenNothing_whenCopyDeckNoCurrentUser_thenThrowIllegalState() {
        Deck simpleDeck = new Deck();
        simpleDeck.setName("Name");

        when(userService.loadCurrentUser()).thenReturn(null);
        assertThrows(IllegalStateException.class, () -> deckService.create(simpleDeck));
    }

    @Test
    public void givenNothing_whenCopyNonExistentDeck_thenThrowDeckNotFound() {
        assertThrows(DeckNotFoundException.class, () -> deckService.copy(0L, getSampleDeck()));
    }

    @Test
    public void givenDeckToCopy_whenCopyDeck_thenReturnCopyOfDeck() {
        RevisionEdit revisionEdit = getSampleRevisionEdit();
        Revision revision = revisionEdit.getRevision();
        Card card = revision.getCard();
        Deck deck = card.getDeck();
        User user = getUnconnectedSampleUser();

        deck.setCategories((new HashSet<>()));
        Category category = getSampleCategoryWithoutParent();
        category.setDecks(new HashSet<>());
        category.getDecks().add(deck);
        deck.getCategories().add(category);

        Deck deckCopy = new Deck();
        deckCopy.setName("copy");

        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).then(returnsFirstArg());
        when(userService.loadCurrentUser()).thenReturn(user);
        when(cardRepository.findCardsWithContentByDeck_Id(deck.getId())).thenReturn(Arrays.asList(card));

        Deck resultDeck = deckService.copy(deck.getId(), deckCopy);
        assertAll(
            () -> assertEquals(resultDeck.getName(), deckCopy.getName()),
            () -> assertEquals(resultDeck.getCreatedBy(), user),
            () -> assertEquals(resultDeck.getCards().size(), deck.getCards().size()),
            () -> assertEquals(resultDeck.getCategories(), deck.getCategories())
        );
    }
}
