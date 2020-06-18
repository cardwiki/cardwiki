package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionEditInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
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
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.textFront").value(FRONT_TEXT))
            .andExpect(jsonPath("$.textBack").value(BACK_TEXT));
    }

    @Test
    public void createCardWithSpecialUtf16CharsReturnsSameText() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(UTF_16_SAMPLE_TEXT);
        dto.setTextBack(UTF_16_SAMPLE_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", deck.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
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

        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
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

        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
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
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", 123)
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(404));
    }

    @Test
    public void createCardWithNullTextThrowsBadRequest() throws Exception {
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(null);
        dto.setTextBack(null);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", 123)
            .with(mockLogin(USER_ROLES, "foo:123"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void createCardWithBlankTextThrowsBadRequest() throws Exception {
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront("  ");
        dto.setTextBack("  ");

        mvc.perform(post("/api/v1/decks/{deckId}/cards", 123)
            .with(mockLogin(USER_ROLES, "foo:123"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void createCardForAnonymousThrowsForbidden() throws Exception {
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/cards", 123)
            .with(mockLogin(ANONYMOUS_ROLES, "foo:123"))
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
        User user = givenApplicationUser();
        mvc.perform(get("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(ANONYMOUS_ROLES, user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.id").value(card.getId()))
            .andExpect(jsonPath("$.textFront").value(revisionEdit.getTextFront()))
            .andExpect(jsonPath("$.textBack").value(revisionEdit.getTextBack()));
    }

    @Test
    @Transactional
    public void getCardWithInvalidDeckIdThrowsNotFoundException() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        Card card = revisionEdit.getRevision().getCard();
        User user = givenApplicationUser();
        mvc.perform(get("/api/v1/decks/{deckId}/cards/{cardId}", 123, card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    @Transactional
    public void getCardWithInvalidCardIdThrowsNotFoundException() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        Deck deck = revisionEdit.getRevision().getCard().getDeck();
        User user = givenApplicationUser();
        mvc.perform(get("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), 123)
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    @Transactional
    public void getCardWithDeckMismatchThrowsNotFoundException() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        Card card = revisionEdit.getRevision().getCard();
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        mvc.perform(get("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    public void editCardReturnsCardDetails() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        User user = givenApplicationUser();
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
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

        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setImageFront(imageDto);
        dto.setImageBack(imageDto);

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
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

        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);
        dto.setImageFront(imageDto);
        dto.setImageBack(imageDto);

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
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
    public void editCardWithInvalidDeckIdThrowsNotFoundException() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", 123, card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(404));
    }

    @Test
    public void editCardWithInvalidCardIdThrowsNotFoundException() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        User user = givenApplicationUser();
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), 123)
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(404));
    }

    @Test
    public void editCardWithNullTextThrowsBadRequest() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(null);
        dto.setTextBack(null);

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(USER_ROLES, "foo:123"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void editCardWithBlankTextThrowsBadRequest() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront("  ");
        dto.setTextBack("  ");

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(USER_ROLES, "foo:123"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
    }

    @Test
    public void editCardForAnonymousThrowsForbidden() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(ANONYMOUS_ROLES, "foo:123"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(403));
    }

    @Test
    public void editCardWithDeckMismatchThrowsNotFoundException() throws Exception {
        Card card = givenCard();
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        RevisionEditInquiryDto dto = new RevisionEditInquiryDto();
        dto.setTextFront(FRONT_TEXT);
        dto.setTextBack(BACK_TEXT);

        mvc.perform(patch("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(404));
    }

    @Test
    public void deleteCardReturnsCardContent() throws Exception {
        Card card = givenCard();
        Deck deck = card.getDeck();
        User user = givenApplicationUser();

        mvc.perform(delete("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void deleteCardWithInvalidDeckIdThrowsNotFoundException() throws Exception {
        Card card = givenCard();
        User user = givenApplicationUser();

        mvc.perform(delete("/api/v1/decks/{deckId}/cards/{cardId}", 123, card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    @Transactional
    public void deleteCardWithInvalidCardIdThrowsNotFoundException() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        Deck deck = revisionEdit.getRevision().getCard().getDeck();
        User user = givenApplicationUser();

        mvc.perform(delete("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), 123)
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(404));
    }

    @Test
    public void deleteCardForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(delete("/api/v1/decks/{deckId}/cards/{cardId}", 123, 123)
            .with(mockLogin(ANONYMOUS_ROLES, "foo:123"))
            .contentType("application/json"))
            .andExpect(status().is(403));
    }

    @Test
    @Transactional
    public void deleteCardWithDeckMismatchThrowsNotFoundException() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();
        Card card = revisionEdit.getRevision().getCard();
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        mvc.perform(delete("/api/v1/decks/{deckId}/cards/{cardId}", deck.getId(), card.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().is(404));
    }
}
