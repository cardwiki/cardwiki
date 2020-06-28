package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest extends TestDataGenerator {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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
            .with(login("foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void cannotRegisterInvalidUsername() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "foo bar");
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .with(login("foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void cannotRegisterTooLongUsername() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "a".repeat(21));
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .with(login("foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void cannotRegisterTooLongDescription() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "test");
        input.put("description", "a".repeat(5001));

        mvc.perform(post("/api/v1/users")
            .with(login("foo:123"))
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

    @Test
    public void cannotRegisterNullToken() throws Exception {
        ObjectNode input = objectMapper.createObjectNode();
        input.put("username", "foo-bar-bar");
        input.put("description", "example");

        mvc.perform(post("/api/v1/users")
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getUserByName() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(get("/api/v1/users/byname/{user}", user.getUsername())
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(user.getUsername()))
            .andExpect(jsonPath("$.description").value(user.getDescription()))
            .andExpect(jsonPath("$.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.updatedAt", validIsoDateTime()));
    }

    @Test
    public void getUserByNameNotFound() throws Exception {
        mvc.perform(get("/api/v1/users/byname/foobar")
            .contentType("application/json"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void searchUsers() throws Exception {
        persistentAgent("fooBar");
        persistentAgent("BuzFooBarBuz");

        mvc.perform(get("/api/v1/users/")
            .queryParam("username", "foobar")
            .queryParam("limit", "10")
            .queryParam("offset", "0")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].username").value("BuzFooBarBuz"))
            .andExpect(jsonPath("$.content[1].username").value("fooBar"));
    }

    @Test
    public void searchNonExistentEmpty() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(get("/api/v1/users/")
            .queryParam("username", user.getUsername()+"moretextsosearchfails")
            .queryParam("limit", "10")
            .queryParam("offset", "0")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    public void getRevisions() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();

        mvc.perform(get("/api/v1/users/{userid}/revisions", revisionEdit.getCreatedBy().getId())
            .queryParam("limit", "10")
            .queryParam("offset", "0")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(revisionEdit.getId()));
    }

    @Test
    public void getDecks() throws Exception {
        Deck deck = givenDeck();

        mvc.perform(get("/api/v1/users/{userid}/decks", deck.getCreatedBy().getId())
            .queryParam("limit", "10")
            .queryParam("offset", "0")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(deck.getId()));
    }

    @Test
    public void editUserDescription() throws Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("description", "updateddescription");

        mvc.perform(patch("/api/v1/users/{userid}", user.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(user.getUsername()))
            .andExpect(jsonPath("$.description").value("updateddescription"));
    }

    @Test
    public void editInvalidAuthForbidden() throws  Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("description", "updateddescription");

        mvc.perform(patch("/api/v1/users/{userid}", user.getId())
            .with(login("foo:123"))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void editOtherUserForbidden() throws  Exception {
        User user1 = givenApplicationUser();
        User user2 = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("description", "updateddescription");

        mvc.perform(patch("/api/v1/users/{userid}", user1.getId())
            .with(login(user2.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminEditAdminStatus() throws Exception {
        User user = givenApplicationUser();
        user.setAdmin(true);
        User editedUser = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("admin", true);

        mvc.perform(patch("/api/v1/users/{userid}", editedUser.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    public void nonAdminEditOwnAdminStatus_forbidden() throws Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("admin", true);

        mvc.perform(patch("/api/v1/users/{userid}", user.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminEditOwnDescription() throws Exception {
        User user = givenApplicationUser();
        user.setAdmin(true);

        ObjectNode input = objectMapper.createObjectNode();
        input.put("description", "updated");

        mvc.perform(patch("/api/v1/users/{userid}", user.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("updated"));
    }

    @Test
    public void nonAdminEditOwnEnabledStatus_forbidden() throws Exception {
        User user = givenApplicationUser();

        ObjectNode input = objectMapper.createObjectNode();
        input.put("enabled", false);

        mvc.perform(patch("/api/v1/users/{userid}", user.getId())
            .with(login(user.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminEditAdmin_forbidden() throws Exception {
        User user1 = givenApplicationUser();
        user1.setAdmin(true);
        User user2 = givenApplicationUser();
        user2.setAdmin(true);

        ObjectNode input = objectMapper.createObjectNode();
        input.put("enabled", false);

        mvc.perform(patch("/api/v1/users/{userid}", user2.getId())
            .with(login(user1.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminDisableUser() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);
        User user = givenApplicationUser();
        user.setEnabled(true);

        ObjectNode input = objectMapper.createObjectNode();
        input.put("enabled", false);
        input.put("reason", "reason");

        mvc.perform(patch("/api/v1/users/{userid}", user.getId())
            .with(login(admin.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isOk());

        mvc.perform(get("/api/v1/auth/whoami")
            .with(login(user.getAuthId()))
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasAccount").value(false));
    }

    @Test
    public void adminDisableUserWithoutReason_badRequest() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);
        User user = givenApplicationUser();
        user.setEnabled(true);

        ObjectNode input = objectMapper.createObjectNode();
        input.put("enabled", false);

        mvc.perform(patch("/api/v1/users/{userid}", user.getId())
            .with(login(admin.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void adminUpdateNonExistentUser_thenThrowNotFound() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);

        ObjectNode input = objectMapper.createObjectNode();
        input.put("description", "updated");

        mvc.perform(patch("/api/v1/users/{userid}", admin.getId() + 1)
            .with(login(admin.getAuthId()))
            .contentType("application/json").content(input.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void userDeleteUser_forbidden() throws Exception {
        User user1 = givenApplicationUser();
        User user2 = givenApplicationUser();

        mvc.perform(delete("/api/v1/users/{userId}?reason=reason", user2.getId())
            .with(login(user1.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminDeleteAdmin_forbidden() throws Exception {
        User admin1 = givenApplicationUser();
        admin1.setAdmin(true);
        User admin2 = givenApplicationUser();
        admin2.setAdmin(true);

        mvc.perform(delete("/api/v1/users/{userId}?reason=reason", admin2.getId())
            .with(login(admin1.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminDeleteNonExistentUser_notFound() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);

        mvc.perform(delete("/api/v1/users/{userId}?reason=reason", admin.getId() + 1)
            .with(login(admin.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void adminDeleteUser() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);
        Progress progress = givenProgress();
        User user = progress.getId().getUser();

        mvc.perform(delete("/api/v1/users/{userId}?reason=reason", user.getId())
            .with(login(admin.getAuthId())))
            .andExpect(status().isNoContent());

        assertFalse(user.isEnabled());
        assertEquals("This user was deleted.", user.getDescription());
        assertEquals("reason", user.getReason());
    }

    @Test
    public void adminDeleteUserWithoutReason_badRequest() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);
        Progress progress = givenProgress();
        User user = progress.getId().getUser();

        mvc.perform(delete("/api/v1/users/{userId}", user.getId())
            .with(login(admin.getAuthId())))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void userExportContainsAllData() throws Exception {
        Agent agent = persistentAgent("some-name");
        User user = agent.getUser();

        Category category = agent.createCategory("foo category");

        Deck deck = agent.createDeck();
        agent.addFavorite(deck);
        agent.addCategory(deck, category);

        Comment comment = agent.createCommentIn(deck, "beautiful deck");

        Image image = agent.createImage("foo.jpg");

        RevisionCreate revisionCreate = new RevisionCreate();
        revisionCreate.setTextBack("back side");
        revisionCreate.setTextFront("front side");
        revisionCreate.setImageFront(image);
        image.getFrontSides().add(revisionCreate);
        revisionCreate.setMessage("created this card");
        Card card = agent.createCardIn(deck, revisionCreate);

        Progress progress = agent.createProgress(card);

        mvc.perform(get("/api/v1/users/{userId}/export", user.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.exportCreatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.user.username").value(user.getUsername()))
            .andExpect(jsonPath("$.user.description").value(user.getDescription()))
            .andExpect(jsonPath("$.user.createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.user.updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.favorites", hasSize(1)))
            .andExpect(jsonPath("$.favorites[0].id").value(deck.getId()))
            .andExpect(jsonPath("$.favorites[0].name").value(deck.getName()))
            .andExpect(jsonPath("$.decks", hasSize(1)))
            .andExpect(jsonPath("$.decks[0].id").value(deck.getId()))
            .andExpect(jsonPath("$.decks[0].name").value(deck.getName()))
            .andExpect(jsonPath("$.categories", hasSize(1)))
            .andExpect(jsonPath("$.categories[0].id").value(category.getId()))
            .andExpect(jsonPath("$.categories[0].name").value(category.getName()))
            .andExpect(jsonPath("$.revisions", hasSize(1)))
            .andExpect(jsonPath("$.revisions[0].id").value(revisionCreate.getId()))
            .andExpect(jsonPath("$.revisions[0].type").value("CREATE"))
            .andExpect(jsonPath("$.revisions[0].cardId").value(card.getId()))
            .andExpect(jsonPath("$.revisions[0].deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.revisions[0].textFront").value(revisionCreate.getTextFront()))
            .andExpect(jsonPath("$.revisions[0].imageFront.filename").value(image.getFilename()))
            .andExpect(jsonPath("$.revisions[0].imageFront.url").isString())
            .andExpect(jsonPath("$.revisions[0].textBack").value(revisionCreate.getTextBack()))
            .andExpect(jsonPath("$.revisions[0].createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.comments", hasSize(1)))
            .andExpect(jsonPath("$.comments[0].deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.comments[0].deck.name").value(deck.getName()))
            .andExpect(jsonPath("$.comments[0].message").value(comment.getMessage()))
            .andExpect(jsonPath("$.comments[0].createdAt", validIsoDateTime()))
            .andExpect(jsonPath("$.comments[0].updatedAt", validIsoDateTime()))
            .andExpect(jsonPath("$.images", hasSize(1)))
            .andExpect(jsonPath("$.images[0].filename").value(image.getFilename()))
            .andExpect(jsonPath("$.images[0].url").isString())
            .andExpect(jsonPath("$.progress", hasSize(1)))
            .andExpect(jsonPath("$.progress[0].cardId").value(card.getId()))
            .andExpect(jsonPath("$.progress[0].latestRevision.deck.id").value(deck.getId()))
            .andExpect(jsonPath("$.progress[0].latestRevision.deck.name").value(deck.getName()))
            .andExpect(jsonPath("$.progress[0].latestRevision.textFront").value(revisionCreate.getTextFront()))
            .andExpect(jsonPath("$.progress[0].latestRevision.textBack").value(revisionCreate.getTextBack()))
            .andExpect(jsonPath("$.progress[0].due", validIsoDateTime()))
            .andExpect(jsonPath("$.progress[0].interval").value(progress.getInterval()))
            .andExpect(jsonPath("$.progress[0].easinessFactor").value(progress.getEasinessFactor()));
    }

    @Test
    public void userExportForAnonymousThrowsForbidden() throws Exception {
        mvc.perform(get("/api/v1/users/{userId}/export", 123L))
            .andExpect(status().isForbidden());
    }

    @Test
    public void userExportForOtherUserThrowsForbidden() throws Exception {
        User user = persistentAgent("me").getUser();
        User otherUser = persistentAgent("other").getUser();

        mvc.perform(get("/api/v1/users/{userId}/export", otherUser.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void userExportAsAdminReturnsOk() throws Exception {
        User admin = persistentAgent("me").makeAdmin().getUser();
        User otherUser = persistentAgent("other").getUser();

        mvc.perform(get("/api/v1/users/{userId}/export", otherUser.getId())
            .with(login(admin.getAuthId())))
            .andExpect(status().isOk());
    }

    @Test
    public void userExportAsAdminThrowsNotFound() throws Exception {
        User admin = persistentAgent("me").makeAdmin().getUser();

        mvc.perform(get("/api/v1/users/{userId}/export", 123L)
            .with(login(admin.getAuthId())))
            .andExpect(status().isNotFound());
    }
}
