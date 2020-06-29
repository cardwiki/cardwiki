package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CommentEndpointTest extends TestDataGenerator {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String UTF_16_SAMPLE_TEXT = "ユ简크로أفضل البحوثΣὲ γνДесแผ∮E⋅∞∑çéèñé";

    private static Stream<String> provideInvalidCommentMessages() {
        return Stream.of(null, "\t ", "x".repeat(501));
    }

    private Comment givenAnyComment() {
        return givenAnyCommentWithMessage("it's a beautiful day");
    }

    private Comment givenAnyCommentWithMessage(String message) {
        Deck deck = persistentAgent("any-deck-creator").createDeck();
        return persistentAgent("any-commentator").createCommentIn(deck, message);
    }

    private Comment givenAnyCommentBy(Agent agent) {
        return agent.createCommentIn(agent.createDeck());
    }

    private String validCommentInputJson() {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", "some random comment");
        return input.toString();
    }

    @ParameterizedTest
    @ValueSource(strings = {"this is a message", UTF_16_SAMPLE_TEXT})
    public void createCommentReturnsCommentSimpleDto(String message) throws Exception {
        Deck deck = persistentAgent("foo").createDeck();
        User user = persistentAgent("bar").getUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", message);

        mvc.perform(post("/api/v1/decks/{deckId}/comments", deck.getId())
            .with(login(user.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.createdBy.id").value(user.getId()))
            .andExpect(jsonPath("$.createdBy.username").value(user.getUsername()));
    }

    @Test
    public void createCommentWithInvalidDeckIdThrowsNotFoundException() throws Exception {
        mvc.perform(post("/api/v1/decks/{deckId}/comments", 0L)
            .with(login(givenUserAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(validCommentInputJson()))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCommentMessages")
    public void createCommentWithInvalidMessageThrowsBadRequest(String message) throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", message);

        mvc.perform(post("/api/v1/decks/{deckId}/comments", 0L)
            .with(login(givenUserAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createCommentForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(post("/api/v1/decks/{deckId}/comments", 0L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(validCommentInputJson()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getCommentReturnsCommentSimpleDto() throws Exception {
        Comment comment = givenAnyComment();
        User creator = comment.getCreatedBy();

        mvc.perform(get("/api/v1/comments/{commentId}", comment.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.message").value(comment.getMessage()))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.createdBy.id").value(creator.getId()))
            .andExpect(jsonPath("$.createdBy.username").value(creator.getUsername()));
    }

    @Test
    public void getCommentForUnknownCommentThrowsNotFound() throws Exception {
        mvc.perform(get("/api/v1/comments/{commentId}", 0L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getCommentsByDeckIdReturnsCommentPage() throws Exception {
        Comment comment = givenAnyComment();
        Deck deck = comment.getDeck();
        User creator = comment.getCreatedBy();

        mvc.perform(get("/api/v1/decks/{deckId}/comments", deck.getId())
            .queryParam("offset", "0")
            .queryParam("limit", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.last").value(true))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.pageable.pageSize").value(1))
            .andExpect(jsonPath("$.content[0].id").value(comment.getId()))
            .andExpect(jsonPath("$.content[0].message").value(comment.getMessage()))
            .andExpect(jsonPath("$.content[0].createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.content[0].updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.content[0].createdBy.id").value(creator.getId()))
            .andExpect(jsonPath("$.content[0].createdBy.username").value(creator.getUsername()));
    }

    @Test
    public void addTwoComments_thenGetCommentsByDeckId_returnsSortedCommentPage() throws Exception {
        Agent agent = persistentAgent();
        Deck deck = agent.createDeck();
        agent.createCommentIn(deck, "first comment");
        agent.createCommentIn(deck, "second comment");

        // Get comments and check if newest message is first
        mvc.perform(get("/api/v1/decks/{deckId}/comments", deck.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].message").value("second comment"))
            .andExpect(jsonPath("$.content[1].message").value("first comment"));
    }

    @Test
    public void getCommentsByDeckIdForUnknownDeckThrowsNotFound() throws Exception {
        mvc.perform(get("/api/v1/decks/{deckId}/comments", 0L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void editCommentReturnsCommentSimpleDto() throws Exception {
        Comment comment = givenAnyCommentWithMessage("old message");
        User creator = comment.getCreatedBy();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", "new fancy message");

        mvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
            .with(login(creator.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(comment.getId()))
            .andExpect(jsonPath("$.message").value("new fancy message"))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.createdBy.id").value(creator.getId()))
            .andExpect(jsonPath("$.createdBy.username").value(creator.getUsername()));
    }

    @Test
    public void editCommentWithUnknownCommentThrowsNotFoundException() throws Exception {
        mvc.perform(put("/api/v1/comments/{commentId}", 0L)
            .with(login(givenUserAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(validCommentInputJson()))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCommentMessages")
    public void editCommentWithInvalidMessageThrowsBadRequest(String message) throws Exception {
        Comment comment = givenAnyComment();
        User creator = comment.getCreatedBy();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", message);

        mvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
            .with(login(creator.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void editCommentForAnonymousThrowsForbidden() throws Exception {
        Comment comment = givenAnyComment();

        mvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(validCommentInputJson()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void editCommentOfOtherUserThrowsForbidden() throws Exception {
        Comment comment = givenAnyCommentBy(persistentAgent("creator"));
        User otherUser = persistentAgent("other").getUser();

        mvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
            .with(login(otherUser.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(validCommentInputJson()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCommentReturnsNoContent() throws Exception {
        Comment comment = givenAnyComment();
        User creator = comment.getCreatedBy();

        mvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .with(login(creator.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCommentThenGetReturnsNotFound() throws Exception {
        Comment comment = givenAnyComment();
        User creator = comment.getCreatedBy();

        mvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .with(login(creator.getAuthId())))
            .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/comments/{commentId}", comment.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCommentForUnknownCommentThrowsNotFound() throws Exception {
        mvc.perform(delete("/api/v1/comments/{commentId}", 0L)
            .with(login(givenUserAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCommentOfOtherUserThrowsForbidden() throws Exception {
        Comment comment = givenAnyCommentBy(persistentAgent("creator"));
        User otherUser = persistentAgent("other").getUser();

        mvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .with(login(otherUser.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCommentAsAdminReturnsNoContent() throws Exception {
        Comment comment = givenAnyCommentBy(persistentAgent("creator"));

        mvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .with(login(givenAdminAuthId())))
            .andExpect(status().isNoContent());
    }
}
