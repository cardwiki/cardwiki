package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.hamcrest.Matchers.hasSize;
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

    private static final String testFilePath = "src/test/resources/test.csv";


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

    @Test
    public void givenAuthenticatedUserAndDeck_whenImportCards_thenReturnOk() throws Exception {
        User user = givenApplicationUser();
        Deck deck = persistentAgent().createDeck();
        FileInputStream fileInputStream = new FileInputStream(testFilePath);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);

        mvc.perform(multipart("/api/v1/decks/" + deck.getId() + "/cards")
            .file(multipartFile)
            .with(login(user.getAuthId())))
            .andExpect(status().isOk());
    }

    @Test
    public void givenNoAuthentication_whenImportCards_thenThrow403() throws Exception {
        Deck deck = persistentAgent().createDeck();
        FileInputStream fileInputStream = new FileInputStream(testFilePath);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);

        mvc.perform(multipart("/api/v1/decks/" + deck.getId() + "/cards")
            .file(multipartFile))
            .andExpect(status().isForbidden());
    }

    @Test
    public void givenDeck_whenExport_thenReturnCsv() throws Exception {
        Deck deck = persistentAgent().createDeck();

        mvc.perform(get("/api/v1/decks/" + deck.getId())
            .header("Accept", "text/csv"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv;charset=UTF-8"));
    }

    @Test
    public void givenNothing_whenExportNonExistentDeck_thenThrowNotFound() throws Exception {
      mvc.perform(get("/api/v1/decks/" + 753L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void givenNothing_whenImportCardsWithUnsupportedContentType_thenThrowBadRequest() throws Exception {
        User user = givenApplicationUser();
        Deck deck = persistentAgent().createDeck();
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);

        mvc.perform(multipart("/api/v1/decks/" + deck.getId() + "/cards")
            .file(multipartFile)
            .with(login(user.getAuthId())))
            .andExpect(status().isBadRequest());
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
    public void getProgress_loggedInDeckExists_returnsOk() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        for (int i = 0; i < 3; i++) {
            agent.createCardIn(deck);
        }
        mvc.perform(get("/api/v1/decks/{id}/progress", deck.getId()).with(login(givenUserAuthId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.newCount").value(3))
            .andExpect(jsonPath("$.learningCount").value(0))
            .andExpect(jsonPath("$.toReviewCount").value(0));
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

        CategorySimpleDto categorySimpleDto = new CategorySimpleDto();
        categorySimpleDto.setId(category.getId());
        categorySimpleDto.setName(category.getName());

        Set<CategorySimpleDto> categories = new HashSet<>();
        categories.add(categorySimpleDto);
        DeckUpdateDto deckUpdateDto = new DeckUpdateDto();
        deckUpdateDto.setCategories(categories);

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

        CategorySimpleDto categorySimpleDto = new CategorySimpleDto();
        categorySimpleDto.setId(category.getId());
        categorySimpleDto.setName(category.getName());

        Set<CategorySimpleDto> categories = new HashSet<>();
        categories.add(categorySimpleDto);
        DeckUpdateDto deckUpdateDto = new DeckUpdateDto();
        deckUpdateDto.setCategories(categories);
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
    public void editDeckForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(patch("/api/v1/decks/{deckId}", 1)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(new DeckUpdateDto())))
            .andExpect(status().is(403));
    }
}
