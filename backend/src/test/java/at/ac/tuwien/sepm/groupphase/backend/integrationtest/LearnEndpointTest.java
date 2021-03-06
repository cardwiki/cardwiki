package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LearnEndpointTest extends TestDataGenerator {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenAuthenticatedUser_whenGetNext_noDeckId_then400() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(
            get("/api/v1/learn/next")
                .queryParam("reverse", "false")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void givenAuthenticatedUser_whenGetNext_noReverseSpecified_then400() throws Exception {
        mvc.perform(
            get("/api/v1/learn/next")
                .queryParam("deckId", "99")
                .with(login(givenUserAuthId()))
                .contentType("application/json")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void givenAuthenticatedUser_whenGetNext_deckMissing_then400() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(
            get("/api/v1/learn/next")
                .queryParam("deckId", "99")
                .queryParam("reverse", "false")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void givenAuthenticatedUser_whenGetNext_deckExists_then200() throws Exception {
        User user = givenApplicationUser();
        Deck deck = givenDeck();

        mvc.perform(
            get("/api/v1/learn/next")
                .queryParam("deckId", deck.getId().toString())
                .queryParam("reverse", "false")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
        ).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    public void givenAuthenticatedUser_whenGetNext_newCard_then200() throws Exception {
        User user = givenApplicationUser();
        RevisionEdit edit = givenRevisionEdit();

        mvc.perform(
            get("/api/v1/learn/next")
                .queryParam("deckId", edit.getCard().getDeck().getId().toString())
                .queryParam("reverse", "false")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
        ).andExpect(status().isOk()).andExpect(jsonPath("$[0].textFront").value(edit.getTextFront()));
    }

    @Test
    public void givenAuthenticatedUser_whenGetNextReverse_dueReverseCard_thenSingleResult() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressDue(card, true);

        mvc.perform(
            get("/api/v1/learn/next")
                .queryParam("deckId", deck.getId().toString())
                .queryParam("reverse", "true")
                .with(login(user.getAuthId()))
                .contentType("application/json")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void givenAuthenticatedUser_andNotDueReverseCard_whenGetNextNormal_thenSingleResult() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressNotDue(card, true);

        mvc.perform(
            get("/api/v1/learn/next")
                .queryParam("deckId", deck.getId().toString())
                .queryParam("reverse", "false")
                .with(login(user.getAuthId()))
                .contentType("application/json")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void givenAuthenticatedUser_whenGetNext_cardNotDue_thenEmpty() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.createProgressNotDue(card, true);

        mvc.perform(
            get("/api/v1/learn/next")
                .queryParam("deckId", deck.getId().toString())
                .queryParam("reverse", "true")
                .with(login(user.getAuthId()))
                .contentType("application/json")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void givenRevisionDelete_whenGetNext_thenEmpty() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);
        agent.deleteCard(card);

        mvc.perform(get("/api/v1/learn/next")
            .queryParam("deckId", deck.getId().toString())
            .queryParam("reverse", "false")
            .with(login(givenUserAuthId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void givenAuthenticatedUser_whenPostAttempt_then201() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);

        ObjectNode input = objectMapper.createObjectNode();
        input.put("cardId", card.getId());
        input.put("status", "AGAIN");

        mvc.perform(
            post("/api/v1/learn/attempt")
                .with(login(givenUserAuthId()))
                .contentType("application/json")
                .content(input.toString())
        )
            .andExpect(status().isCreated());
    }

    @Test
    public void givenAuthenticatedUser_whenPostAttemptReverse_thenFindNext_isEmpty() throws Exception {
        Agent agent = persistentAgent();
        User user = agent.getUser();
        Deck deck = agent.createDeck();
        Card card = agent.createCardIn(deck);

        ObjectNode input = objectMapper.createObjectNode();
        input.put("cardId", card.getId());
        input.put("status", "AGAIN");
        input.put("reverse", "true");

        mvc.perform(post("/api/v1/learn/attempt")
            .with(login(user.getAuthId()))
            .contentType("application/json")
            .content(input.toString()))
            .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/learn/next")
            .queryParam("deckId", deck.getId().toString())
            .queryParam("reverse", "true")
            .with(login(user.getAuthId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void givenNoAuthentication_whenPostAttempt_then403() throws Exception {
        Card card = givenCard();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("cardId", card.getId());
        input.put("status", "AGAIN");

        mvc.perform(
            post("/api/v1/learn/attempt")
                .contentType("application/json")
                .content(input.toString())
        )
            .andExpect(status().isForbidden());
    }

    @Test
    public void givenAuthenticatedUser_whenAttemptNonexistentCard_then400() throws Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("cardId", "404");
        input.put("status", "AGAIN");

        mvc.perform(
            post("/api/v1/learn/attempt")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
                .content(input.toString())
        )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenAuthenticatedUser_whenInvalidStatus_then400() throws Exception {
        User user = givenApplicationUser();
        Card card = givenCard();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("cardId", card.getId());
        input.put("status", "---");

        mvc.perform(
            post("/api/v1/learn/attempt")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
                .content(input.toString())
        )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenAuthenticatedUser_whenNullStatus_then400() throws Exception {
        User user = givenApplicationUser();
        Card card = givenCard();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("cardId", card.getId());

        mvc.perform(
            post("/api/v1/learn/attempt")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
                .content(input.toString())
        )
            .andExpect(status().isBadRequest());
    }
}
