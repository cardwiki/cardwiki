package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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

    // TODO: test user updating

    @Test
    public void createUser() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "test");
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .with(login("foo:123"))
            .contentType("application/json").content(input.toString()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.description").value("example"))
        .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
        .andExpect(jsonPath("$.updatedAt", validIsoDateTime()));
    }

    @Test
    public void cannotRegisterWithoutUsername() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void cannotRegisterInvalidUsername() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "foo bar");
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void cannotRegisterTooLongUsername() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "a".repeat(21));
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void cannotRegisterTooLongDescription() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "test");
        input.put("description", "a".repeat(5001));

        mvc.perform(post("/api/v1/users")
            .with(mockLogin(USER_ROLES, "foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void cannotRegisterDuplicateUsername() throws Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", user.getUsername());
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .with(login("foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isConflict());

    }

    @Test
    public void cannotRegisterDuplicateAuthId() throws Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "some-username");
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .with(login(user.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isConflict());

    }
}
