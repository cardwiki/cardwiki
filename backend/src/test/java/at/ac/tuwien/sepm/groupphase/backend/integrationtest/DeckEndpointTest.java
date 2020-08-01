package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionCreate;
import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
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

    @ParameterizedTest
    @ValueSource(strings = {"test front,test back", "front,back\nfront2,back2"})
    public void givenAuthenticatedUserAndDeck_whenImportCards_thenReturnOk(String content) throws Exception {
        Deck deck = persistentAgent().createDeck();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testFile.csv", "text/csv", content.getBytes());

        mvc.perform(multipart("/api/v1/decks/{deckId}/cards", deck.getId())
            .file(multipartFile)
            .with(login(givenUserAuthId())))
            .andExpect(status().isOk());

        long lines = 1 + content.chars().filter(c -> c == '\n').count();
        assertEquals(lines, deck.getCards().size(), "Created one card per line");
    }

    @Test
    public void givenAuthenticatedUserAndDeck_whenImportExistingCards_thenReturnOk() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.setMessage("Created");
        revisionCreate.setTextFront("Existing Front");
        revisionCreate.setTextBack("Random Back");
        agent.createCardIn(deck, revisionCreate);

        MockMultipartFile multipartFile = new MockMultipartFile("file", "testFile.csv", "text/csv",
                                                                "Existing Front,Not existing Back".getBytes());

        mvc.perform(multipart("/api/v1/decks/{deckId}/cards", deck.getId())
            .file(multipartFile)
            .with(login(givenUserAuthId())))
            .andExpect(status().isOk());

        assertEquals(1, deck.getCards().size(), "Import ignored existing card");
    }

    @ParameterizedTest
    @ValueSource(strings = {"too few columns", "   ,blank", "blank,\t", "too,many,columns"})
    public void givenAuthenticatedUserAndDeck_whenImportInvalidCards_thenThrowBadRequest(String content) throws Exception {
        Deck deck = persistentAgent().createDeck();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testFile.csv", "text/csv", content.getBytes());

        mvc.perform(multipart("/api/v1/decks/{deckId}/cards", deck.getId())
            .file(multipartFile)
            .with(login(givenUserAuthId())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNoAuthentication_whenImportCards_thenThrowForbidden() throws Exception {
        Deck deck = persistentAgent().createDeck();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testFile.csv", "text/csv", "test front,test back".getBytes());

        mvc.perform(multipart("/api/v1/decks/{deckId}/cards", deck.getId())
            .file(multipartFile))
            .andExpect(status().isForbidden());
    }

    @Test
    public void givenDeck_whenExport_thenReturnCsv() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.setTextFront("front side");
        revisionCreate.setTextBack("back side");
        revisionCreate.setMessage("test message");
        agent.createCardIn(deck, revisionCreate);

        mvc.perform(get("/api/v1/decks/" + deck.getId())
            .header("Accept", "text/csv"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv;charset=UTF-8"))
            .andExpect(content().string(containsString("front side,back side")));
    }

    @Test
    public void givenNothing_whenExportNonExistentDeck_thenThrowNotFound() throws Exception {
      mvc.perform(get("/api/v1/decks/" + 753L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getRevisions_deckDoesNotExist_returnsNotFound() throws Exception {
        mvc.perform(get("/api/v1/decks/123/revisions"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getRevisions_deckIsEmpty_returnsEmptyArray() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        mvc.perform(get("/api/v1/decks/{id}/revisions", deck.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", Matchers.hasSize(0)));
    }

    @Test
    public void getRevisions_hasRevisions_returnsRevisions() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        for (int i = 0; i < 9; i++) {
            Card card = agent.createCardIn(deck);
            for (int j = 0; j < 9; j++) {
                agent.editCard(card);
            }
        }
        // TODO: check content of content
        mvc.perform(get("/api/v1/decks/{id}/revisions?limit=100", deck.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", Matchers.hasSize(90)));
    }

    @Test
    public void getDeckReturnsDeck() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();

        mvc.perform(get("/api/v1/decks/{id}", deck.getId()))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").value(deck.getId()))
            .andExpect(jsonPath("$.name").value(deck.getName()))
            .andExpect(jsonPath("$.createdBy").value(deck.getCreatedBy().getId()))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.categories").isEmpty());
    }

    @Test
    public void getDeckWithCategoriesReturnsDeckWithCategories() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        Category category = agent.createCategory("test category");
        deck = agent.addCategory(deck, category);

        mvc.perform(get("/api/v1/decks/{id}", deck.getId()))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").value(deck.getId()))
            .andExpect(jsonPath("$.name").value(deck.getName()))
            .andExpect(jsonPath("$.createdBy").value(deck.getCreatedBy().getId()))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.categories", hasSize(1)))
            .andExpect(jsonPath("$.categories[0].id").value(category.getId()))
            .andExpect(jsonPath("$.categories[0].name").value(category.getName()));
    }

    @Test
    public void getDeckWithInvalidDeckIdThrowsNotFoundException() throws Exception {
        mvc.perform(get("/api/v1/decks/{deckId}", 123)
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    public void editDeckWithUpdatedNameReturnsDeckWithUpdatedName() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        DeckUpdateDto dto = new DeckUpdateDto();
        dto.setName("test deck");

        mvc.perform(patch("/api/v1/decks/{deckId}", deck.getId())
            .with(login(agent.getUser().getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").value(deck.getId()))
            .andExpect(jsonPath("$.name").value(dto.getName()))
            .andExpect(jsonPath("$.createdBy").value(deck.getCreatedBy().getId()))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.categories").isEmpty());
    }

    @Test
    public void editDeckWithUpdatedCategoriesReturnsDeckWithUpdatedCategories() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        Category category = agent.createCategory("test category");

        DeckUpdateDto deckUpdateDto = new DeckUpdateDto();
        deckUpdateDto.setCategories(Collections.singleton(category.getId()));

        mvc.perform(patch("/api/v1/decks/{deckId}", deck.getId())
            .with(login(agent.getUser().getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(deckUpdateDto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").value(deck.getId()))
            .andExpect(jsonPath("$.name").value(deck.getName()))
            .andExpect(jsonPath("$.createdBy").value(deck.getCreatedBy().getId()))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.categories", hasSize(1)))
            .andExpect(jsonPath("$.categories[0].id").value(category.getId()))
            .andExpect(jsonPath("$.categories[0].name").value(category.getName()));
    }

    @Test
    public void editDeckWithUpdatedNameAndCategoriesReturnsDeckWithUpdatedNameAndCategories() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        Category category = agent.createCategory("test category");

        DeckUpdateDto deckUpdateDto = new DeckUpdateDto();
        deckUpdateDto.setCategories(Collections.singleton(category.getId()));
        deckUpdateDto.setName("test deck");

        mvc.perform(patch("/api/v1/decks/{deckId}", deck.getId())
            .with(login(agent.getUser().getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(deckUpdateDto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").value(deck.getId()))
            .andExpect(jsonPath("$.name").value(deckUpdateDto.getName()))
            .andExpect(jsonPath("$.createdBy").value(deck.getCreatedBy().getId()))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.categories", hasSize(1)))
            .andExpect(jsonPath("$.categories[0].id").value(category.getId()))
            .andExpect(jsonPath("$.categories[0].name").value(category.getName()));
    }

    @Test
    public void editDeckWithInvalidDeckIdThrowsNotFoundException() throws Exception {
        Agent agent = persistentAgent();

        mvc.perform(patch("/api/v1/decks/{deckId}", 123)
            .with(login(agent.getUser().getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(new DeckUpdateDto())))
            .andExpect(status().is(404));
    }

    @Test
    public void editDeckWithBlankNameThrowsBadRequest() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();

        DeckUpdateDto dto = new DeckUpdateDto();
        dto.setName("");

        mvc.perform(patch("/api/v1/decks/{deckId}", deck.getId())
            .with(login(agent.getUser().getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void givenDeck_editDeckWithNullCategory_throwsBadRequest() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        Category category = agent.createCategory("foo");

        mvc.perform(patch("/api/v1/decks/{deckId}", deck.getId())
            .with(login(givenUserAuthId()))
            .contentType("application/json")
            .content("{ \"categories\": [null] }"))
            .andExpect(status().is(400));
    }

    @Test
    public void editDeckForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(patch("/api/v1/decks/{deckId}", 1)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(new DeckUpdateDto())))
            .andExpect(status().is(403));
    }

    @Test
    public void whenCardInDeckHasProgress_thenGetLearnedDecks_returnsDecksWithProgress() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card1 = agent.createCardIn(deck);
        Card card2 = agent.createCardIn(deck);
        Card card3 = agent.createCardIn(deck);
        Card card4 = agent.createCardIn(deck);
        agent.createProgressNotDue(card2, Progress.Status.LEARNING, false);
        agent.createProgressNotDue(card3, Progress.Status.REVIEWING, false);
        agent.createProgressNotDue(card4, Progress.Status.REVIEWING, false);
        agent.createProgressNotDue(card4, Progress.Status.REVIEWING, true);

        mvc.perform(get("/api/v1/decks/progress")
            .with(login(user.getAuthId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].deckId").value(deck.getId()))
            .andExpect(jsonPath("$.content[0].deckName").value(deck.getName()))
            .andExpect(jsonPath("$.content[0].normal.totalCount").value(4))
            .andExpect(jsonPath("$.content[0].normal.newCount").value(1))
            .andExpect(jsonPath("$.content[0].normal.learningCount").value(1))
            .andExpect(jsonPath("$.content[0].normal.dueCount").value(1))
            .andExpect(jsonPath("$.content[0].reverse.totalCount").value(4))
            .andExpect(jsonPath("$.content[0].reverse.newCount").value(3))
            .andExpect(jsonPath("$.content[0].reverse.learningCount").value(0))
            .andExpect(jsonPath("$.content[0].reverse.dueCount").value(3));
    }

    @Test
    public void whenNoReverse_thenGetLearnedDecks_returnsDecksWithNoReverseProgress() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressNotDue(card, Progress.Status.LEARNING, false);

        mvc.perform(get("/api/v1/decks/progress")
            .with(login(user.getAuthId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].normal").exists())
            .andExpect(jsonPath("$.content[0].reverse").doesNotExist());
    }

    @Test
    public void getLearnedDecksForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(get("/api/v1/decks/progress"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserProgressReturnsNoContent() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();

        mvc.perform(delete("/api/v1/decks/{deckId}/progress", deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserProgressReverseReturnsNoContent() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();

        mvc.perform(delete("/api/v1/decks/{deckId}/progress", deck.getId())
            .queryParam("reverse", "true")
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserProgressForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(delete("/api/v1/decks/{deckId}/progress", 123L))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteUserProgressForUnknownDeckThrowsNotFound() throws Exception {
        mvc.perform(delete("/api/v1/decks/{deckId}/progress", 123L)
            .with(login(givenUserAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserProgressThenGetLearnedDeckReturnsEmpty() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressNotDue(card, Progress.Status.LEARNING, true);

        mvc.perform(delete("/api/v1/decks/{deckId}/progress", deck.getId())
            .queryParam("reverse", "true")
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/decks/progress")
            .with(login(user.getAuthId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty());
    }
}
