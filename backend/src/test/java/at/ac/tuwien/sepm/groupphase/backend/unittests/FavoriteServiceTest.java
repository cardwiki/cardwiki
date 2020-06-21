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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

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
        User user = new User(0L);
        Deck deck = mock(Deck.class);

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        Deck result = favoriteService.addFavorite(user.getId(), deck.getId());

        assertEquals(deck, result, "Returns favorite deck");
        assertTrue(user.getFavorites().contains(deck), "Deck has been added to user favorites");
    }

    @Test
    void givenFavorite_whenAddFavorite_thenThrowConflictException() {
        User user = new User(0L);
        Deck deck = mock(Deck.class);
        user.getFavorites().add(deck);

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);

        assertThrows(ConflictException.class, () -> favoriteService.addFavorite(user.getId(), deck.getId()));
    }

    @Test
    void givenNothing_whenAddFavoriteWithoutAuthentication_thenThrowAuthenticationRequiredException() {
        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);

        assertThrows(AuthenticationRequiredException.class, () -> favoriteService.addFavorite(0L, 1L));
    }

    @Test
    void givenNothing_whenAddFavoriteForOtherUser_thenThrowInsufficientAuthorizationException() {
        Long userId = 0L;
        Long otherUserId = 1L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));

        assertThrows(InsufficientAuthorizationException.class, () -> favoriteService.addFavorite(otherUserId, 2L));
    }

    @Test
    void givenNothing_whenAddUnknownDeck_thenThrowDeckNotFoundException() {
        Long userId = 0L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));
        when(deckService.findOneOrThrow(any(Long.class))).thenThrow(DeckNotFoundException.class);

        assertThrows(DeckNotFoundException.class, () -> favoriteService.addFavorite(userId, 0L));
    }

    @Test
    void givenFavorite_whenGetFavorites_thenReturnFavorite() {
        Long userId = 0L;
        Pageable pageable = mock(Pageable.class);
        Page<Deck> page = new PageImpl<>(new ArrayList<>());

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));
        when(deckRepository.findByFavoredById(userId, pageable)).thenReturn(page);

        Page<Deck> result = favoriteService.getFavorites(userId, pageable);
        assertEquals(page, result);
    }

    @Test
    void givenFavorite_whenGetFavoritesWithoutAuthentication_thenThrowAuthenticationRequiredException() {
        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);

        assertThrows(AuthenticationRequiredException.class, () -> favoriteService.getFavorites(0L, mock(Pageable.class)));
    }

    @Test
    void givenFavorite_whenGetFavoritesForOtherUser_thenThrowInsufficientAuthorizationException() {
        Long userId = 0L;
        Long otherUserId = 1L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));

        assertThrows(InsufficientAuthorizationException.class, () -> favoriteService.getFavorites(otherUserId, mock(Pageable.class)));
    }

    @Test
    void givenFavorite_whenHasFavorite_thenReturnTrue() {
        Long userId = 0L;
        Long deckId = 1L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));
        when(deckRepository.existsByIdAndFavoredById(deckId, userId)).thenReturn(true);

        assertTrue(favoriteService.hasFavorite(userId, deckId));
    }

    @Test
    void givenUser_whenHasFavorite_thenReturnFalse() {
        Long userId = 0L;
        Deck deck = new Deck();
        deck.setId(1L);

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));
        when(deckRepository.existsByIdAndFavoredById(deck.getId(), userId)).thenReturn(false);

        assertFalse(favoriteService.hasFavorite(userId, deck.getId()));
    }

    @Test
    void givenUser_whenHasFavoriteWithUnknownDeck_thenThrowDeckNotFoundException() {
        Long userId = 0L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));
        when(deckService.findOneOrThrow(any(Long.class))).thenThrow(DeckNotFoundException.class);

        assertThrows(DeckNotFoundException.class, () -> favoriteService.hasFavorite(userId, 1L));
    }

    @Test
    void givenUser_whenHasFavoriteWithoutAuthentication_thenThrowAuthenticationRequiredException() {
        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);

        assertThrows(AuthenticationRequiredException.class, () -> favoriteService.hasFavorite(0L, 1L));
    }

    @Test
    void givenUser_whenHasFavoriteForOtherUser_thenThrowInsufficientAuthorizationException() {
        Long userId = 0L;
        Long otherUserId = 1L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));

        assertThrows(InsufficientAuthorizationException.class, () -> favoriteService.hasFavorite(otherUserId, 2L));
    }

    @Test
    void givenFavorite_whenRemoveFavorite_thenSucceed() {
        User user = new User(0L);
        Deck deck = new Deck();
        deck.setId(1L);
        user.getFavorites().add(deck);

        when(userService.loadCurrentUserOrThrow()).thenReturn(user);
        when(deckService.findOneOrThrow(deck.getId())).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        favoriteService.removeFavorite(user.getId(), deck.getId());

        assertFalse(user.getFavorites().contains(deck), "Deck has been added to user favorites");
    }

    @Test
    void givenFavorite_whenRemoveFavoriteWithoutAuthentication_thenThrowAuthenticationRequiredException() {
        when(userService.loadCurrentUserOrThrow()).thenThrow(AuthenticationRequiredException.class);

        assertThrows(AuthenticationRequiredException.class, () -> favoriteService.removeFavorite(0L, 1L));
    }

    @Test
    void givenFavorite_whenRemoveFavoriteForOtherUser_thenThrowInsufficientAuthorizationException() {
        Long userId = 0L;
        Long otherUserId = 1L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));

        assertThrows(InsufficientAuthorizationException.class, () -> favoriteService.removeFavorite(otherUserId, 2L));
    }

    @Test
    void givenNothing_whenRemoveUnknownDeck_thenThrowDeckNotFoundException() {
        Long userId = 0L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));
        when(deckService.findOneOrThrow(any(Long.class))).thenThrow(DeckNotFoundException.class);

        assertThrows(DeckNotFoundException.class, () -> favoriteService.removeFavorite(userId, 0L));
    }

    @Test
    void givenNothing_whenRemoveNonFavorite_thenThrowDeckNotFoundException() {
        Long userId = 0L;
        Deck deck = new Deck();
        deck.setId(1L);
        Long favoriteId = 2L;

        when(userService.loadCurrentUserOrThrow()).thenReturn(new User(userId));
        when(deckService.findOneOrThrow(any(Long.class))).thenReturn(deck);
        when(userRepository.save(any(User.class))).thenAnswer(returnsFirstArg());

        assertThrows(DeckNotFoundException.class, () -> favoriteService.removeFavorite(userId, favoriteId));
    }

}
