package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
    public void givenAuthenticatedUser_whenPostAttempt_then201() throws Exception {
        User user = givenApplicationUser();
        Card card = givenCard();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("cardId", card.getId());
        input.put("status", "AGAIN");
        mvc.perform(
            post("/api/v1/learn/attempt")
                .with(mockLogin(USER_ROLES, user.getAuthId()))
                .contentType("application/json")
                .content(input.toString())
        )
            .andExpect(status().isCreated());
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
    public void a() throws Exception {
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
}
