package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.stream.Collectors;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class DeckEndpointTest extends TestDataGenerator {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private CardRepository cardRepository;

    @Test
    public void givenAuthenticatedUser_whenCreateDeck_thenReturnDeck() throws Exception {
        User user = givenApplicationUser();
        String deckName = "TeStNaME";
        ObjectNode input = objectMapper.createObjectNode();
        input.put("name", deckName);

        mvc.perform(
            post("/api/v1/decks")
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(input.toString())
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(deckName))
            .andExpect(jsonPath("$.createdBy").value(user.getId()))
            .andExpect(jsonPath("$.createdAt").value(validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt").value(validIsoDateTime()));
    }

    @Test
    public void givenNoAuthentication_whenCreateDeck_then403() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("name", "test");

        mvc.perform(
            post("/api/v1/decks")
            .contentType("application/json")
            .content(input.toString())
        ).andExpect(status().isForbidden());
    }

    @Test
    public void givenAuthenticatedUser_whenCreateDeckNameTooLong_then400() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("name", "A".repeat(1024));

        mvc.perform(
            post("/api/v1/decks")
                .with(mockLogin(USER_ROLES, "x:0"))
                .contentType("application/json")
                .content(input.toString())
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void givenAuthenticatedUser_whenCreateDeckBlankName_then400() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("name", " \t");

        mvc.perform(
            post("/api/v1/decks")
                .with(mockLogin(USER_ROLES, "x:0"))
                .contentType("application/json")
                .content(input.toString())
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void givenAuthenticatedUser_whenCreateDeckUnicodeName_thenReturnDeck() throws Exception {
        User user = givenApplicationUser();
        String deckName = "\u82B1";
        ObjectNode input = objectMapper.createObjectNode();
        input.put("name", deckName);

        mvc.perform(
            post("/api/v1/decks")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
                .content(input.toString())
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value(deckName))
            .andExpect(jsonPath("$.createdBy").value(user.getId()))
            .andExpect(jsonPath("$.createdAt").value(validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt").value(validIsoDateTime()));
    }

    @Test
    public void givenNoAuthentication_whenSearchDecks_thenReturnDeck() throws Exception {
        Deck deck = givenDeck();

        mvc.perform(
            get("/api/v1/decks")
                .contentType("application/json")
                .queryParam("name", deck.getName())
                .queryParam("limit", "10")
                .queryParam("offset", "0")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].id").value(deck.getId()))
            .andExpect(jsonPath("$.content[0].name").value(deck.getName()))
            .andExpect(jsonPath("$.content[0].createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.content[0].updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.content[0].createdBy").value(deck.getCreatedBy().getId()))
            .andExpect(jsonPath("$.content[0].categories").isEmpty()); // TODO: Test with categories
    }

    @Test
    public void givenNothing_whenSearchNonexistentName_thenReturnEmptyList() throws Exception {
        mvc.perform(
            get("/api/v1/decks")
                .contentType("application/json")
                .queryParam("name", "nonexistent")
                .queryParam("limit", "10")
                .queryParam("offset", "0")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    public void givenNothing_whenSearchUnicode_thenReturnDeck() throws Exception {
        Deck deck = givenDeck();
        deck.setName("\u82B1");
        deck = deckRepository.saveAndFlush(deck);

        mvc.perform(
            get("/api/v1/decks")
                .contentType("application/json")
                .queryParam("name", deck.getName())
                .queryParam("limit", "10")
                .queryParam("offset", "0")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.content[0].id").value(deck.getId()))
            .andExpect(jsonPath("$.content[0].name").value(deck.getName()));
    }

    @Test
    public void givenAuthenticatedUser_whenCopyDeck_thenReturnDeckCopy() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        String deckName = "copy dummy";
        ObjectNode input = objectMapper.createObjectNode();
        input.put("name", deckName);

        mvc.perform(
            post("/api/v1/decks/{id}/copy", deck.getId())
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
                .content(input.toString())
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.createdAt").value(validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt").value(validIsoDateTime()))
            .andExpect(jsonPath("$.name").value(deckName))
            .andExpect(jsonPath("$.createdBy").value(user.getId()))
            .andExpect(jsonPath("$.categories").value(deck.getCategories().stream()
                .map((x) -> {
                    CategorySimpleDto category = new CategorySimpleDto();
                    category.setName(x.getName());
                    category.setId(x.getId());
                    return category;
                }).collect(Collectors.toList())));

    }

    @Test
    public void givenAuthenticatedUser_whenCopyDeckBlankName_thenThrowBadRequest() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        ObjectNode input = objectMapper.createObjectNode();
        input.put("name", " \t");

        mvc.perform(
            post("/api/v1/decks/{id}/copy", deck.getId())
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
                .content(input.toString())
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void givenNothing_whenCopyNonexistentDeck_thenThrowNotFound() throws Exception {
        User user = givenApplicationUser();
        ObjectNode input = objectMapper.createObjectNode();
        input.put("name", "blubb");

        mvc.perform(
            post("/api/v1/decks/{id}/copy", 0L)
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
                .content(input.toString())
        ).andExpect(status().isNotFound());
    }

    @Test
    public void givenAuthenticatedAdmin_whenDeleteNonExistent_then404() throws Exception {
        User user = givenApplicationUser();
        user.setAdmin(true);

        mvc.perform(
            delete("/api/v1/decks/1")
                .with(login(user.getAuthId()))
        )
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenNoAuthentication_whenDeleteDeck_then403() throws Exception {
        Deck deck = givenDeck();

        mvc.perform(
            delete("/api/v1/decks/" + deck.getId())
        ).andExpect(status().isForbidden());
    }

    @Test
    public void givenAuthenticatedUser_whenDeleteDeck_then403() throws Exception {
        User user = givenApplicationUser();
        Deck deck = givenDeck();

        mvc.perform(
            delete("/api/v1/decks/" + deck.getId())
            .with(login(user.getAuthId()))
        ).andExpect(status().isForbidden());
    }

    @Test
    public void givenAuthenticatedAdmin_whenDeleteDeck_thenDeckDeleted() throws Exception {
        User user = givenApplicationUser();
        user.setAdmin(true);
        Card card = givenCard();

        long deckId = card.getDeck().getId();
        long cardId = card.getId();

        mvc.perform(
            delete("/api/v1/decks/" + deckId)
            .with(login(user.getAuthId()))
        ).andExpect(status().isNoContent());

        assertTrue(deckRepository.findById(deckId).isEmpty());
        assertTrue(cardRepository.findById(cardId).isEmpty());
    }
}
