package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FavoriteEndpointTest extends TestDataGenerator {
    @Autowired
    private MockMvc mvc;

    private Deck givenAnyDeck() {
        return persistentAgent("given-any-deck-creat").createDeck();
    }

    @Test
    public void addFavorite() throws Exception {
        Deck deck = givenAnyDeck();
        User user = persistentAgent().getUser();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(deck.getId()))
            .andExpect(jsonPath("name").value(deck.getName()));
    }

    @Test
    public void addFavoriteWhichIsAlreadyFavoriteThrowsConflict() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.addFavorite(givenAnyDeck());
        User user = agent.getUser();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isConflict());
    }

    @Test
    public void addFavorite_withNoAuthentication_throwsForbidden() throws Exception {
        Deck deck = givenAnyDeck();
        User user = persistentAgent().getUser();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void addFavorite_forOtherUser_throwsForbidden() throws Exception {
        Deck deck = givenAnyDeck();
        User user = persistentAgent("first").getUser();
        User otherUser = persistentAgent("other").getUser();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", otherUser.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void addFavorite_withUnknownDeckId_throwsNotFound() throws Exception {
        User user = persistentAgent().getUser();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), 0L)
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getFavorites() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck favorite = agent.addFavorite(givenAnyDeck());

        mvc.perform(get("/api/v1/users/{userId}/favorites", user.getId())
            .queryParam("offset", "0")
            .queryParam("limit", "10")
            .with(login(user.getAuthId())))
            .andExpect(status().isOk())
            .andExpect((jsonPath("content[0].id").value(favorite.getId())))
            .andExpect((jsonPath("content[0].name").value(favorite.getName())))
            .andExpect(jsonPath("numberOfElements").value(1))
            .andExpect(jsonPath("totalElements").value(1))
            .andExpect(jsonPath("totalPages").value(1))
            .andExpect(jsonPath("first").value(true))
            .andExpect(jsonPath("last").value(true))
            .andExpect(jsonPath("pageable.pageNumber").value(0))
            .andExpect(jsonPath("pageable.pageSize").value(10));
    }

    @Test
    public void getFavorites_withNoAuthentication_throwsForbidden() throws Exception {
        mvc.perform(get("/api/v1/users/{userId}/favorites", 0L)
            .queryParam("limit", "10")
            .queryParam("offset", "0"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getFavorites_forOtherUser_throwsForbidden() throws Exception {
        User user = persistentAgent("first").getUser();
        User otherUser = persistentAgent("other").getUser();

        mvc.perform(get("/api/v1/users/{userId}/favorites", otherUser.getId())
            .queryParam("limit", "10")
            .queryParam("offset", "0")
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void hasFavorite_returnsNoContent() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck favorite = agent.addFavorite(givenAnyDeck());

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), favorite.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void hasFavorite_whenNotFavorite_throwNotFound() throws Exception {
        User user = persistentAgent().getUser();
        Deck deck = givenAnyDeck();

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void hasFavorite_whenUnknownDeck_throwNotFound() throws Exception {
        User user = persistentAgent().getUser();

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), 0L)
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void hasFavorite_withNoAuthentication_throwsForbidden() throws Exception {
        User user = persistentAgent().getUser();
        Deck deck = givenAnyDeck();

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void hasFavorite_forOtherUser_throwsForbidden() throws Exception {
        User user = persistentAgent("first").getUser();
        User otherUser = persistentAgent("other").getUser();
        Deck deck = givenAnyDeck();

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", otherUser.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void removeFavorite() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck favorite = agent.addFavorite(givenAnyDeck());

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), favorite.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void removeFavorite_withUnknownDeckId_throwsNotFound() throws Exception {
        User user = persistentAgent().getUser();

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), 0L)
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void removeFavorite_withNoFavorite_throwsNotFound() throws Exception {
        User user = persistentAgent().getUser();
        Deck deck = givenAnyDeck();

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void removeFavorite_withNoAuthentication_throwsForbidden() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck favorite = agent.addFavorite(givenAnyDeck());

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), favorite.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void removeFavorite_forOtherUser_throwsForbidden() throws Exception {
        Agent agent = persistentAgent("first");
        User user = agent.getUser();
        Deck favorite = agent.addFavorite(givenAnyDeck());
        User otherUser = persistentAgent("other").getUser();

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", otherUser.getId(), favorite.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }
}
