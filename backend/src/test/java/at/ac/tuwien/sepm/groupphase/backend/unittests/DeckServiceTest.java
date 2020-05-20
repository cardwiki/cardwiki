package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class DeckServiceTest extends TestDataGenerator {
    @MockBean
    private DeckRepository deckRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private DeckService deckService;

    private static final Long EXISTENT_ID = 1L;
    private static final Long NONEXISTENT_ID = 2L;
    private final Deck DECK;
    private final User USER;
    private static final String OAUTH_ID = "oauth_id";
    private static final String NONEXISTENT_OAUTH_ID = "smh";

    public DeckServiceTest() {
        User user = new User();
        user.setUsername("username");
        user.setOAuthId(OAUTH_ID);
        USER = user;
        Deck deck = new Deck();
        deck.setId(1L);
        deck.setName(DECK_NAME);
        deck.setCreatedBy(USER);
        deck.setCreatedAt(LocalDateTime.now());
        deck.setUpdatedAt(LocalDateTime.now());
        DECK = deck;
    }

    @BeforeEach
    public void setup() {
        Mockito.when(
            deckRepository.findByNameContainingIgnoreCase(DECK_NAME, null)
        ).thenReturn(Collections.singletonList(DECK));
        Mockito.when(
            deckRepository.findByNameContainingIgnoreCase("", null)
        ).thenReturn(Collections.emptyList());
        Mockito.when(
            deckRepository.findById(EXISTENT_ID)
        ).thenReturn(Optional.of(DECK));
        Mockito.when(
            deckRepository.findById(NONEXISTENT_ID)
        ).thenReturn(Optional.empty());
        Mockito.when(
            deckRepository.save(any())
        ).thenReturn(DECK);
        Mockito.when(
            userService.loadUserByOauthId(OAUTH_ID)
        ).thenReturn(USER);
        Mockito.when(
            userService.loadUserByOauthId(NONEXISTENT_OAUTH_ID)
        ).thenThrow(new NotFoundException());
    }

    @Test
    public void givenNothing_whenFindOneNonexistent_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> deckService.findOne(NONEXISTENT_ID));
    }

    @Test
    public void givenNothing_whenFindOneExistent_thenReturnDeck() {
        assertEquals(DECK, deckService.findOne(EXISTENT_ID));
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
        assertTrue(deckService.searchByName("", null).isEmpty());
    }

    @Test
    public void givenNothing_whenSearchByNameExistent_thenReturnDeck() {
        assertTrue(deckService.searchByName(DECK_NAME, null).contains(DECK));
    }

    @Test
    public void givenNothing_whenCreate_thenReturnDeckWithCorrectUser() {
        Deck deck = new Deck();
        deck.setName("Name");
        assertEquals(DECK, deckService.create(deck, OAUTH_ID));
    }

    @Test
    public void givenNothing_whenCreateNonExistentOAuthId_thenThrowNotFound() {
        Deck deck = new Deck();
        deck.setName("Name");
        assertThrows(NotFoundException.class, () -> deckService.create(deck, NONEXISTENT_OAUTH_ID));
    }

    @Test
    public void givenNothing_whenCreateArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> deckService.create(null, null));
        assertThrows(NullPointerException.class, () -> deckService.create(null, OAUTH_ID));
        assertThrows(NullPointerException.class, () -> deckService.create(DECK, null));
    }
}
