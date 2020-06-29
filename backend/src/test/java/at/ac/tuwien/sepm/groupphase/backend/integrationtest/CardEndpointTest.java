package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CardEndpointTest extends TestDataGenerator {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${cawi.image-served-path}")
    private String imageServedPath;

    private static final String FRONT_TEXT = "Test Front";
    private static final String BACK_TEXT = "Back Front";

    // TODO: Test getCardsByDeckId

    @Test
    public void createCardReturnsCardDetails() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.textFront").value(FRONT_TEXT))
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT));
    }

    @Test
    public void createCardWithMessageReturnsCardDetails() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        RevisionInputDto dto = new RevisionInputDto();
        String message = "this is my message";
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        dto.setMessage(message);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.textFront").value(FRONT_TEXT))
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT));
    }

    @Test
    public void createCardWithTooLongMessageThrowsBadRequest() throws Exception {
        Deck deck = givenDeck();
        RevisionInputDto dto = new RevisionInputDto();
        String message = "x".repeat(Revision.MAX_MESSAGE_SIZE + 1);
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        dto.setMessage(message);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createCardWithSpecialUtf16CharsReturnsSameText() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(UTF_16_SAMPLE_TEXT);
        dto.setTextBack(UTF_16_SAMPLE_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.textFront").value(UTF_16_SAMPLE_TEXT))
            .andExpect(jsonPath("$.textBack").value(UTF_16_SAMPLE_TEXT));
    }

    @Test
    public void createCardWithImagesReturnsCardDetailsWithImageDtos() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Image image = givenImage();

        RevisionInputDto dto = new RevisionInputDto();
        dto.setImageFrontFilename(image.getFilename());
        dto.setImageBackFilename(image.getFilename());

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.imageFrontUrl").value(Paths.get(imageServedPath, dto.getImageFrontFilename()).toString().replace('\\', '/')))
            .andExpect(jsonPath("$.imageBackUrl").value(Paths.get(imageServedPath, dto.getImageFrontFilename()).toString().replace('\\', '/')));
    }

    @Test
    public void createCardWithTextAndImagesReturnsCardDetails() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Image image = givenImage();

        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        dto.setImageFrontFilename(image.getFilename());
        dto.setImageBackFilename(image.getFilename());

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.textFront").value(FRONT_TEXT))
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT))
            .andExpect(jsonPath("$.imageFrontUrl").value(Paths.get(imageServedPath, dto.getImageFrontFilename()).toString().replace('\\', '/')))
            .andExpect(jsonPath("$.imageBackUrl").value(Paths.get(imageServedPath, dto.getImageFrontFilename()).toString().replace('\\', '/')));
    }

    @Test
    public void createCardWithNonExistingImageThrowsBadRequest() throws Exception {
        Agent user = persistentAgent();
        Deck deck = user.createDeck();

        RevisionInputDto dto = new RevisionInputDto();
        dto.setImageFrontFilename("i don't exist");
        dto.setImageBackFilename("me neither");

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(login(user.getUser().getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void createCardWithInvalidDeckIdThrowsNotFoundException() throws Exception {
        User user = givenApplicationUser();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", 123)
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(404));
    }

    @Test
    public void createCardWithoutContentThrowsBadRequest() throws Exception {
        Agent user = persistentAgent();
        Deck deck = user.createDeck();

        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(null);
        dto.setTextBack(null);
        dto.setImageFrontFilename(null);
        dto.setImageBackFilename(null);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(login(user.getUser().getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void createCardWithBlankTextThrowsBadRequest() throws Exception {
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront("  ");
        dto.setTextBack("  ");

        mvc.perform(post("/api/v1/decks/{deckId}/cards", 123)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void createCardForAnonymousThrowsForbidden() throws Exception {
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", 123)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(403));
    }

    @Test
    @Transactional
    public void getCardReturnsCardUpdate() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        Card card = revisionEdit.getCard();
        mvc.perform(get("/api/v1/cards/{cardId}", card.getId())
            .contentType("application/json"))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.textFront").value(revisionEdit.getTextFront()))
            .andExpect(jsonPath("$.textBack").value(revisionEdit.getTextBack()))
            .andExpect(jsonPath("$.imageFront.filename").value(revisionEdit.getImageFront().getFilename()))
            .andExpect(jsonPath("$.imageFront.url").value(Paths.get(imageServedPath, revisionEdit.getImageFront().getFilename()).toString()))
            .andExpect(jsonPath("$.imageBack.filename").value(revisionEdit.getImageBack().getFilename()))
            .andExpect(jsonPath("$.imageBack.url").value(Paths.get(imageServedPath, revisionEdit.getImageBack().getFilename()).toString()));
    }

    @Test
    @Transactional
    public void getCardWithInvalidCardIdThrowsNotFoundException() throws Exception {
        mvc.perform(get("/api/v1/cards/{cardId}", 123)
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    public void editCardReturnsCardDetails() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        User user = givenApplicationUser();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.textFront").value(FRONT_TEXT))
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT));
    }

    @Test
    public void editCardWithNewImagesReturnsCardDetails() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        User user = givenApplicationUser();

        RevisionInputDto dto = new RevisionInputDto();
        Image image = givenImage();
        dto.setImageFrontFilename(image.getFilename());
        dto.setImageBackFilename(image.getFilename());

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.imageFrontUrl").value(Paths.get(imageServedPath, dto.getImageFrontFilename()).toString().replace('\\', '/')))
            .andExpect(jsonPath("$.imageBackUrl").value(Paths.get(imageServedPath, dto.getImageFrontFilename()).toString().replace('\\', '/')));
    }

    @Test
    public void editCardWithTextAndNewImagesReturnsCardDetails() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        User user = givenApplicationUser();

        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        Image image = givenImage();
        dto.setImageFrontFilename(image.getFilename());
        dto.setImageBackFilename(image.getFilename());

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.textFront").value(FRONT_TEXT))
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT))
            .andExpect(jsonPath("$.imageFrontUrl").value(Paths.get(imageServedPath, dto.getImageFrontFilename()).toString().replace('\\', '/')))
            .andExpect(jsonPath("$.imageBackUrl").value(Paths.get(imageServedPath, dto.getImageFrontFilename()).toString().replace('\\', '/')));
    }

    @Test
    public void editCardWithInvalidCardIdThrowsNotFoundException() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(patch("/api/v1/cards/{cardId}", 123)
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(404));
    }

    @Test
    public void editCardWithoutContentThrowsBadRequest() throws Exception {
        Agent user = persistentAgent();
        Deck deck = user.createDeck();
        Card card = user.createCardIn(deck);

        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(null);
        dto.setTextBack(null);
        dto.setImageFrontFilename(null);
        dto.setImageBackFilename(null);

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .with(login(user.getUser().getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void editCardWithBlankTextThrowsBadRequest() throws Exception {
        Card card = givenCard();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront("  ");
        dto.setTextBack("  ");

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void editCardForAnonymousThrowsForbidden() throws Exception {
        Card card = givenCard();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(403));
    }

    @Test
    public void editCardWithMessageReturnsOk() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        dto.setMessage("this is my message");

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());
    }

    @Test
    public void editCardWithTooLongMessageThrowsBadRequest() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        dto.setMessage("x".repeat(Revision.MAX_MESSAGE_SIZE + 1));

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCardReturnsNoContent() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();

        mvc.perform(post("/api/v1/cards/{cardId}", card.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

    @Test
    @Transactional
    public void deleteCardWithInvalidCardIdThrowsNotFoundException() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        User user = givenApplicationUser();

        mvc.perform(post("/api/v1/cards/{cardId}", 123)
            .with(login(user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    public void deleteCardForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(post("/api/v1/cards/{cardId}", 123)
            .contentType("application/json"))
            .andExpect(status().is(403));
    }

    @Test
    public void deleteCardWithMessageReturnsNoContent() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();
        String message = "this is my message";

        mvc.perform(post("/api/v1/cards/{cardId}", card.getId())
            .queryParam("message", message)
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

    @Test
    public void deleteCardWithTooLongMessageThrowsBadRequest() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();
        String message = "x".repeat(Revision.MAX_MESSAGE_SIZE + 1);

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", message);

        mvc.perform(post("/api/v1/cards/{cardId}", card.getId())
            .contentType("application/json")
            .content(input.toString())
            .with(login(user.getAuthId())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void userDeletesCardReturnsNoContent() throws Exception {
        User user = givenApplicationUser();
        Card card = givenCard();

        mvc.perform(delete("/api/v1/cards/{cardId}", card.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void adminDeletesNonExistentCardReturnsNotFound() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);

        mvc.perform(delete("/api/v1/cards/{cardId}", 404)
            .with(login(admin.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void adminDeletesCardReturnsNoContent() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);
        Card card = givenCard();

        mvc.perform(delete("/api/v1/cards/{cardId}", card.getId())
            .with(login(admin.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void getRevisionsOfCard_cardDoesNotExist_returnsNotFound() throws Exception {
        mvc.perform(get("/api/v1/cards/123/revisions"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getRevisionsOfCard_cardExists_revisionsAreReturned() throws Exception {
        Agent agent = persistentAgent();
        Card card = agent.createCardIn(agent.createDeck());
        for (int i = 0; i < 9; i++) {
            agent.editCard(card);
        }
        // TODO: check content of content
        mvc.perform(get("/api/v1/cards/{id}/revisions", card.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", Matchers.hasSize(10)));
    }

    @Test
    public void getLatestRevisions_returnsOk() throws Exception {
        mvc.perform(get("/api/v1/revisions"))
            .andExpect(status().isOk());
    }

    @Test
    public void getRevisionsByIds_noIds_returnsBadRequest() throws Exception {
        mvc.perform(get("/api/v1/revisions/byid"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getRevisionsByIds_nonExistantId_returnsOk() throws Exception {
        mvc.perform(get("/api/v1/revisions/byid").queryParam("id", "99"))
            .andExpect(status().isOk());
    }

    @Test
    public void getRevisionsByIds_existingIds_returnsRevisions() throws Exception {
        Agent agent = persistentAgent();
        Card card = agent.createCardIn(agent.createDeck());
        MockHttpServletRequestBuilder builder = get("/api/v1/revisions/byid");
        for (int i = 0; i < 10; i++) {
            agent.editCard(card);
            builder.queryParam("id", String.valueOf(card.getLatestRevision().getId()));
        }
        // TODO: check content
        mvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*", Matchers.hasSize(10)));
    }

    @Test
    public void getRevisionsByIds_tooManyIds_returnsBadRequest() throws Exception {
        Agent agent = persistentAgent();
        Card card = agent.createCardIn(agent.createDeck());
        MockHttpServletRequestBuilder builder = get("/api/v1/revisions/byid");
        for (int i = 0; i < 11; i++) {
            agent.editCard(card);
            builder.queryParam("id", String.valueOf(card.getLatestRevision().getId()));
        }
        // TODO: check content
        mvc.perform(builder)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getCardsByDeckId_emptyDeck_returnsEmptyArray() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        mvc.perform(get("/api/v1/decks/{id}/cards", deck.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", Matchers.hasSize(0)));
    }
}
