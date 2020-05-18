package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest extends TestDataGenerator {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    // TODO: test more thoroughly

    @Test
    public void createUser() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "test");
        input.put("description", "example");
        input.put("admin", false);

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "123"))
            .contentType("application/json").content(input.toString()))
        .andExpect(status().is(201))
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.description").value("example"))
        .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
        .andExpect(jsonPath("$.updatedAt", validIsoDateTime()));
    }

    @Test
    public void createUserNoUsername() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("description", "example");
        input.put("admin", false);

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().is(400));
    }

    @Test
    public void createUserInvalidUsername() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "foo bar");
        input.put("description", "example");
        input.put("admin", false);

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().is(400));
    }

    @Test
    public void createUserTooLongUsername() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "a".repeat(21));
        input.put("description", "example");
        input.put("admin", false);

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().is(400));
    }

    @Test
    public void createUserTooLongDescription() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "a".repeat(5001));
        input.put("description", "example");
        input.put("admin", false);

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().is(400));
    }
}
