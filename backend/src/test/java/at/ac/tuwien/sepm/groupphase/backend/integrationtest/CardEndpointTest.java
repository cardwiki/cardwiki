package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionEditInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
            .with(mockLogin(USER_ROLES, "foo"))
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
            .with(mockLogin(USER_ROLES, "foo"))
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
            .with(mockLogin(ANONYMOUS_ROLES, "foo"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(403));
    }
}
