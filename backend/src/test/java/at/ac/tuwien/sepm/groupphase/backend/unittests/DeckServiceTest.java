package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
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
        assertThrows(NotFoundException.class, () -> deckService.findOneOrThrow(id));
    }

    @Test
    public void givenNothing_whenFindOneExistent_thenReturnDeck() {
        Long id = 1L;
        Deck deck = getSampleDeck();
        Mockito.when(deckRepository.findById(id)).thenReturn(Optional.of(deck));
        assertEquals(deck, deckService.findOneOrThrow(id));
    }

    @Test
    public void givenNothing_whenFindOneArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> deckService.findOneOrThrow(null));
    }

    @Test
    public void givenNothing_whenSearchByNameArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> deckService.searchByName(null, Pageable.unpaged()));
    }

    @Test
    public void givenNothing_whenSearchByNameNotExistent_thenReturnEmptyList() {
        Mockito.when(deckRepository.findByNameContainingIgnoreCase("", Pageable.unpaged()))
            .thenReturn(new PageImpl<>(Collections.emptyList()));
        assertTrue(deckService.searchByName("", Pageable.unpaged()).isEmpty());
    }

    @Test
    public void givenNothing_whenSearchByNameExistent_thenReturnDeck() {
        Deck deck = mock(Deck.class);
        Mockito.when(deckRepository.findByNameContainingIgnoreCase("foo", Pageable.unpaged()))
            .thenReturn(new PageImpl<>(Collections.singletonList(deck)));
        assertTrue(deckService.searchByName("foo", Pageable.unpaged()).getContent().contains(deck));
    }

    @Test
    public void givenNothing_whenCreate_thenReturnDeckWithCorrectUser() {
        Deck deck = getSampleDeck();
        Deck simpleDeck = new Deck();
        simpleDeck.setName(deck.getName());

        Mockito.when(deckRepository.save(simpleDeck)).thenReturn(deck);
        Mockito.when(userService.loadCurrentUserOrThrow()).thenReturn(deck.getCreatedBy());
        assertEquals(deck, deckService.create(simpleDeck));
    }

    @Test
    public void givenNothing_whenCreateNoCurrentUser_thenThrowAuthenticationRequiredException() {
        Deck deck = getSampleDeck();
        Deck simpleDeck = new Deck();
        simpleDeck.setName("Name");

        Mockito.when(deckRepository.save(simpleDeck)).thenReturn(deck);
        Mockito.when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);
        assertThrows(AuthenticationRequiredException.class, () -> deckService.create(simpleDeck));
    }

    @Test
    public void givenNothing_whenCreateArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> deckService.create(null));
    }

    @Test
    public void givenNothing_whenCopyDeckNoCurrentUser_thenThrowAuthenticationRequiredException() {
        Deck simpleDeck = new Deck();
        simpleDeck.setName("Name");

        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);
        assertThrows(AuthenticationRequiredException.class, () -> deckService.create(simpleDeck));
    }

    @Test
    public void givenNothing_whenCopyNonExistentDeck_thenThrowDeckNotFound() {
        assertThrows(DeckNotFoundException.class, () -> deckService.copy(0L, getSampleDeck()));
    }

    @Test
    public void givenDeckToCopy_whenCopyDeck_thenReturnCopyOfDeck() {
        Agent current = transientAgent("marie");
        Agent gustav = transientAgent("gustav");
        Deck deck = gustav.createDeck();

        for (int i = 0; i < 10; i++) {
            gustav.createCardIn(deck);
        }

        long id = 1;

        when(deckRepository.findById(id)).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).then(returnsFirstArg());
        when(userService.loadCurrentUserOrThrow()).thenReturn(current.getUser());
        when(cardRepository.findCardsWithContentByDeck_Id(id)).thenReturn(deck.getCards().stream());

        Deck resultDeck = deckService.copy(id, deck);
        assertAll(
            () -> assertEquals(resultDeck.getName(), deck.getName()),
            () -> assertEquals(resultDeck.getCreatedBy(), current.getUser()),
            () -> assertEquals(resultDeck.getCards().size(), 10),
            () -> assertEquals(resultDeck.getCategories(), deck.getCategories())
        );
    }
}
