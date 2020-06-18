package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Disabled;
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
import java.util.Arrays;
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

    @ParameterizedTest
    @ValueSource(strings = {"this is a message", UTF_16_SAMPLE_TEXT})
    public void createCommentReturnsCommentSimpleDto(String message) throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();

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
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", UTF_16_SAMPLE_TEXT);

        mvc.perform(post("/api/v1/decks/{deckId}/comments", 0L)
            .with(login(user.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCommentMessages")
    public void createCommentWithInvalidMessageThrowsBadRequest(String message) throws Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", message);

        mvc.perform(post("/api/v1/decks/{deckId}/comments", 0L)
            .with(login(user.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void createCommentForAnonymousThrowsForbidden() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", "foo");

        mvc.perform(post("/api/v1/decks/{deckId}/comments", 0L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getCommentReturnsCommentSimpleDto() throws Exception {
        Comment comment = givenComment();
        User expectedUser = comment.getCreatedBy();

        mvc.perform(get("/api/v1/comments/{commentId}", comment.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.message").value(comment.getMessage()))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.createdBy.id").value(expectedUser.getId()))
            .andExpect(jsonPath("$.createdBy.username").value(expectedUser.getUsername()));
    }

    @Test
    public void getCommentForUnknownCommentThrowsNotFound() throws Exception {
        mvc.perform(get("/api/v1/comments/{commentId}", 0L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getCommentsByDeckIdReturnsCommentPage() throws Exception {
        Comment comment = givenComment();
        Deck deck = comment.getDeck();

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
            .andExpect(jsonPath("$.content[0].createdBy.id").value(comment.getCreatedBy().getId()))
            .andExpect(jsonPath("$.content[0].createdBy.username").value(comment.getCreatedBy().getUsername()));
    }

    @Test
    public void addTwoComments_thenGetCommentsByDeckId_returnsSortedCommentPage() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        String firstMessage = "this is a message";
        String secondMessage = "this is a message";

        // Create comments
        for (String message : Arrays.asList(firstMessage, secondMessage)) {
            ObjectNode input = objectMapper.createObjectNode();
            input.put("message", message);

            mvc.perform(post("/api/v1/decks/{deckId}/comments", deck.getId())
                .with(login(user.getAuthId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(input.toString()))
                .andExpect(status().isCreated());
        }

        // Get comments and check if newest message is first
        mvc.perform(get("/api/v1/decks/{deckId}/comments", deck.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numberOfElements").value(2))
            .andExpect(jsonPath("$.last").value(true))
            .andExpect(jsonPath("$.content[0].message").value(secondMessage))
            .andExpect(jsonPath("$.content[1].message").value(firstMessage));
    }

    @Test
    public void getCommentsByDeckIdForUnknownDeckThrowsNotFound() throws Exception {
        mvc.perform(get("/api/v1/decks/{deckId}/comments", 0L))
            .andExpect(status().isNotFound());
    }

    @Test
    public void editCommentReturnsCommentSimpleDto() throws Exception {
        Comment comment = givenComment();
        User user = comment.getCreatedBy();
        String newMessage = "new fancy message";

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", newMessage);

        mvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
            .with(login(user.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(comment.getId()))
            .andExpect(jsonPath("$.message").value(newMessage))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$[?(@.createdAt < @.updatedAt)]").isNotEmpty())
            .andExpect(jsonPath("$.createdBy.id").value(user.getId()))
            .andExpect(jsonPath("$.createdBy.username").value(user.getUsername()));
    }

    @Test
    public void editCommentWithUnknownCommentThrowsNotFoundException() throws Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", "foo");

        mvc.perform(put("/api/v1/comments/{commentId}", 0L)
            .with(login(user.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCommentMessages")
    public void editCommentWithNullMessageThrowsBadRequest(String message) throws Exception {
        Comment comment = givenComment();
        User user = comment.getCreatedBy();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", message);

        mvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
            .with(login(user.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void editCommentForAnonymousThrowsForbidden() throws Exception {
        Comment comment = givenComment();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", "foo");

        mvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void editCommentOfOtherUserThrowsForbidden() throws Exception {
        Comment comment = givenComment();
        User otherUser = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("message", "foo");

        mvc.perform(put("/api/v1/comments/{commentId}", comment.getId())
            .with(login(otherUser.getAuthId()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCommentReturnsNoContent() throws Exception {
        Comment comment = givenComment();
        User user = comment.getCreatedBy();

        mvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCommentThenGetReturnsNotFound() throws Exception {
        Comment comment = givenComment();
        User user = comment.getCreatedBy();

        mvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/comments/{commentId}", comment.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCommentForUnknownCommentThrowsNotFound() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(delete("/api/v1/comments/{commentId}", 0L)
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCommentOfOtherUserThrowsForbidden() throws Exception {
        Comment comment = givenComment();
        User otherUser = givenApplicationUser();

        mvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .with(login(otherUser.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCommentAsAdminReturnsNoContent() throws Exception {
        Comment comment = givenComment();
        User admin = givenAdmin();

        mvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .with(login(admin.getAuthId())))
            .andExpect(status().isNoContent());
    }
}
