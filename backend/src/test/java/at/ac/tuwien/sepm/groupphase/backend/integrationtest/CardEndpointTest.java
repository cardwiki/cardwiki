package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Paths;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private static final String UTF_16_SAMPLE_TEXT = "ユ简크로أفضل البحوثΣὲ γνДесแผ∮E⋅∞∑çéèñé";
    private static final String FRONT_TEXT = "Test Front";
    private static final String BACK_TEXT = "Back Front";

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
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT))
            .andExpect(jsonPath("$.revisions[0].message").value(message));
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

    @Test void createCardWithImagesReturnsCardDetailsWithImageDtos() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Image image = givenImage();

        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setFilename(image.getFilename());

        RevisionInputDto dto = new RevisionInputDto();
        dto.setImageFront(imageDto);
        dto.setImageBack(imageDto);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.imageFront.filename").value(imageDto.getFilename()))
            .andExpect(jsonPath("$.imageBack.filename").value(imageDto.getFilename()))
            .andExpect(jsonPath("$.imageFront.url").value(Paths.get(imageServedPath, imageDto.getFilename()).toString()))
            .andExpect(jsonPath("$.imageBack.url").value(Paths.get(imageServedPath, imageDto.getFilename()).toString()));
    }

    @Test void createCardWithTextAndImagesReturnsCardDetails() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Image image = givenImage();

        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setFilename(image.getFilename());

        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        dto.setImageFront(imageDto);
        dto.setImageBack(imageDto);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.textFront").value(FRONT_TEXT))
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT))
            .andExpect(jsonPath("$.imageFront.filename").value(imageDto.getFilename()))
            .andExpect(jsonPath("$.imageBack.filename").value(imageDto.getFilename()))
            .andExpect(jsonPath("$.imageFront.url").value(Paths.get(imageServedPath, imageDto.getFilename()).toString()))
            .andExpect(jsonPath("$.imageBack.url").value(Paths.get(imageServedPath, imageDto.getFilename()).toString()));
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
    public void createCardWithNullTextThrowsBadRequest() throws Exception {
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(null);
        dto.setTextBack(null);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", 123)
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
    public void getCardReturnsCardSimple() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        Card card = revisionEdit.getRevision().getCard();
        Deck deck = card.getDeck();
        mvc.perform(get("/api/v1/cards/{cardId}", card.getId())
            .contentType("application/json"))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").value(card.getId()))
            .andExpect(jsonPath("$.textFront").value(revisionEdit.getTextFront()))
            .andExpect(jsonPath("$.textBack").value(revisionEdit.getTextBack()));
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
        Image image = givenImage();

        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setFilename(image.getFilename());

        RevisionInputDto dto = new RevisionInputDto();
        dto.setImageFront(imageDto);
        dto.setImageBack(imageDto);

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.imageFront.filename").value(imageDto.getFilename()))
            .andExpect(jsonPath("$.imageBack.filename").value(imageDto.getFilename()))
            .andExpect(jsonPath("$.imageFront.url").value(Paths.get(imageServedPath, imageDto.getFilename()).toString()))
            .andExpect(jsonPath("$.imageBack.url").value(Paths.get(imageServedPath, imageDto.getFilename()).toString()));
    }

    @Test
    public void editCardWithTextAndNewImagesReturnsCardDetails() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        User user = givenApplicationUser();
        Image image = givenImage();

        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setFilename(image.getFilename());

        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        dto.setImageFront(imageDto);
        dto.setImageBack(imageDto);

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.textFront").value(FRONT_TEXT))
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT))
            .andExpect(jsonPath("$.imageFront.filename").value(imageDto.getFilename()))
            .andExpect(jsonPath("$.imageBack.filename").value(imageDto.getFilename()))
            .andExpect(jsonPath("$.imageFront.url").value(Paths.get(imageServedPath, imageDto.getFilename()).toString()))
            .andExpect(jsonPath("$.imageBack.url").value(Paths.get(imageServedPath, imageDto.getFilename()).toString()));
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
    public void editCardWithNullTextThrowsBadRequest() throws Exception {
        Card card = givenCard();
        RevisionInputDto dto = new RevisionInputDto();
        dto.setTextFront(null);
        dto.setTextBack(null);

        mvc.perform(patch("/api/v1/cards/{cardId}", card.getId())
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
    public void deleteCardReturnsCardContent() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();

        mvc.perform(delete("/api/v1/cards/{cardId}", card.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    @Transactional
    public void deleteCardWithInvalidCardIdThrowsNotFoundException() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        User user = givenApplicationUser();

        mvc.perform(delete("/api/v1/cards/{cardId}", 123)
            .with(login(user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    public void deleteCardForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(delete("/api/v1/cards/{cardId}", 123)
            .contentType("application/json"))
            .andExpect(status().is(403));
    }

    @Test
    public void deleteCardWithMessageReturnsOk() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();
        String message = "this is my message";

        mvc.perform(delete("/api/v1/cards/{cardId}", card.getId())
            .queryParam("message", message)
            .with(login(user.getAuthId())))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteCardWithTooLongMessageThrowsBadRequest() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();
        String message = "x".repeat(Revision.MAX_MESSAGE_SIZE + 1);

        mvc.perform(delete("/api/v1/cards/{cardId}", card.getId())
            .queryParam("message", message)
            .with(login(user.getAuthId())))
            .andExpect(status().isBadRequest());
    }
}
