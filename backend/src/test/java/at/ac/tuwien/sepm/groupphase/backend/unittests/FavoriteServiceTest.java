package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.InsufficientAuthorizationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.FavoriteService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class FavoriteServiceTest extends TestDataGenerator {
    @MockBean
    private DeckRepository deckRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private DeckService deckService;

    @Autowired
    private FavoriteService favoriteService;

    @Test
    void givenNothing_whenAddFavorite_thenReturnDeck() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        Deck result = favoriteService.addFavorite(user.getId(), deck.getId());

        assertEquals(deck, result, "Returns favorite deck");
        assertTrue(user.getFavorites().contains(deck), "Deck has been added to user favorites");
    }

    @Test
    void givenFavorite_whenAddFavorite_thenThrowConflictException() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();
        user.getFavorites().add(deck);

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(ConflictException.class, () -> favoriteService.addFavorite(user.getId(), deck.getId()));
    }

    @Test
    void givenNothing_whenAddFavoriteWithoutAuthentication_thenThrowAuthenticationRequiredException() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(AuthenticationRequiredException.class, () -> favoriteService.addFavorite(user.getId(), deck.getId()));
    }

    @Test
    void givenNothing_whenAddFavoriteForOtherUser_thenThrowInsufficientAuthorizationException() {
        User user = getSampleUser();
        Long otherUserId = user.getId() + 1;
        Deck deck = getSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(InsufficientAuthorizationException.class, () -> favoriteService.addFavorite(otherUserId, deck.getId()));
    }

    @Test
    void givenNothing_whenAddUnknownDeck_thenThrowDeckNotFoundException() {
        User user = getSampleUser();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(any(Long.class))).thenThrow(DeckNotFoundException.class);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(DeckNotFoundException.class, () -> favoriteService.addFavorite(user.getId(), 0L));
    }

    @Test
    void givenFavorite_whenGetFavorites_thenReturnFavorite() {
        User user = getSampleUser();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Deck> page = mock(Page.class);

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckRepository.findByFavoredById(user.getId(), pageable)).thenReturn(page);

        Page<Deck> result = favoriteService.getFavorites(user.getId(), pageable);
        assertEquals(page, result);
    }

    @Test
    void givenFavorite_whenGetFavoritesWithoutAuthentication_thenThrowAuthenticationRequiredException() {
        User user = getSampleUser();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Deck> page = mock(Page.class);

        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);
        when(deckRepository.findByFavoredById(user.getId(), pageable)).thenReturn(page);

        assertThrows(AuthenticationRequiredException.class, () -> favoriteService.getFavorites(user.getId(), pageable));
    }

    @Test
    void givenFavorite_whenGetFavoritesForOtherUser_thenThrowInsufficientAuthorizationException() {
        User user = getSampleUser();
        Long otherUserId = user.getId() + 1;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Deck> page = mock(Page.class);

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckRepository.findByFavoredById(user.getId(), pageable)).thenReturn(page);

        assertThrows(InsufficientAuthorizationException.class, () -> favoriteService.getFavorites(otherUserId, pageable));
    }

    @Test
    void givenFavorite_whenHasFavorite_thenReturnTrue() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckRepository.existsByIdAndFavoredById(deck.getId(), user.getId())).thenReturn(true);

        assertTrue(favoriteService.hasFavorite(user.getId(), deck.getId()));
    }

    @Test
    void givenUser_whenHasFavorite_thenReturnFalse() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckRepository.existsByIdAndFavoredById(deck.getId(), user.getId())).thenReturn(false);

        assertFalse(favoriteService.hasFavorite(user.getId(), deck.getId()));
    }

    @Test
    void givenUser_whenHasFavoriteWithUnknownDeck_thenThrowDeckNotFoundException() {
        User user = getSampleUser();
        Long deckId = 0L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(any(Long.class))).thenThrow(DeckNotFoundException.class);
        when(deckRepository.existsByIdAndFavoredById(deckId, user.getId())).thenReturn(false);

        assertThrows(DeckNotFoundException.class, () -> favoriteService.hasFavorite(user.getId(), deckId));
    }

    @Test
    void givenUser_whenHasFavoriteWithoutAuthentication_thenThrowAuthenticationRequiredException() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);
        when(deckRepository.existsByIdAndFavoredById(deck.getId(), user.getId())).thenReturn(false);

        assertThrows(AuthenticationRequiredException.class, () -> favoriteService.hasFavorite(user.getId(), deck.getId()));
    }

    @Test
    void givenUser_whenHasFavoriteForOtherUser_thenThrowInsufficientAuthorizationException() {
        User user = getSampleUser();
        Long otherUserId = user.getId() + 1;
        Deck deck = getSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckRepository.existsByIdAndFavoredById(deck.getId(), user.getId())).thenReturn(false);

        assertThrows(InsufficientAuthorizationException.class, () -> favoriteService.hasFavorite(otherUserId, deck.getId()));
    }

    @Test
    void givenFavorite_whenRemoveFavorite_thenSucceed() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();
        user.getFavorites().add(deck);

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        favoriteService.removeFavorite(user.getId(), deck.getId());

        assertFalse(user.getFavorites().contains(deck), "Deck has been added to user favorites");
    }

    @Test
    void givenFavorite_whenRemoveFavoriteWithoutAuthentication_thenThrowAuthenticationRequiredException() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();

        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(AuthenticationRequiredException.class, () -> favoriteService.removeFavorite(user.getId(), deck.getId()));
    }

    @Test
    void givenFavorite_whenRemoveFavoriteForOtherUser_thenThrowInsufficientAuthorizationException() {
        User user = getSampleUser();
        Long otherUserId = user.getId() + 1;
        Deck deck = getSampleDeck();
        user.getFavorites().add(deck);

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(InsufficientAuthorizationException.class, () -> favoriteService.removeFavorite(otherUserId, deck.getId()));
    }

    @Test
    void givenNothing_whenRemoveUnknownDeck_thenThrowDeckNotFoundException() {
        User user = getSampleUser();

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(any(Long.class))).thenThrow(DeckNotFoundException.class);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(DeckNotFoundException.class, () -> favoriteService.removeFavorite(user.getId(), 0L));
    }

    @Test
    void givenNothing_whenRemoveNonFavorite_thenThrowDeckNotFoundException() {
        User user = getSampleUser();
        Deck deck = getSampleDeck();
        Long favoriteId = deck.getId() + 1;

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(any(Long.class))).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(DeckNotFoundException.class, () -> favoriteService.removeFavorite(user.getId(), favoriteId));
    }

}
