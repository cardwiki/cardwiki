package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

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
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].username").value("BuzFooBarBuz"))
            .andExpect(jsonPath("$[1].username").value("fooBar"));
    }

    @Test
    public void searchNonExistantEmpty() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(get("/api/v1/users/")
            .queryParam("username", user.getUsername()+"moretextsosearchfails")
            .queryParam("limit", "10")
            .queryParam("offset", "0")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    public void getRevisions() throws Exception {
        RevisionEdit revisionEdit = givenRevisionEdit();

        mvc.perform(get("/api/v1/users/{userid}/revisions", revisionEdit.getCreatedBy().getId())
            .queryParam("limit", "10")
            .queryParam("offset", "0")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(revisionEdit.getId()));
    }

    @Test
    public void getDecks() throws Exception {
        Deck deck = givenDeck();

        mvc.perform(get("/api/v1/users/{userid}/decks", deck.getCreatedBy().getId())
            .queryParam("limit", "10")
            .queryParam("offset", "0")
            .contentType("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(deck.getId()));
    }

    public void addFavorite() throws Exception {
        Deck deck = givenDeck();
        User user = deck.getCreatedBy();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(deck.getId()))
            .andExpect(jsonPath("name").value(deck.getName()));
    }

    @Test
    public void addFavoriteWhichIsAlreadyFavoriteThrowsConflict() throws Exception {
        Deck deck = givenFavorite();
        User user = deck.getCreatedBy();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isConflict());
    }

    @Test
    public void addFavorite_withNoAuthentication_throwsForbidden() throws Exception {
        Deck deck = givenDeck();
        User user = deck.getCreatedBy();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void addFavorite_forOtherUser_throwsForbidden() throws Exception {
        Deck deck = givenDeck();
        User user = deck.getCreatedBy();
        Long otherUserId = user.getId() + 1;

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", otherUserId, deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void addFavorite_withUnknownDeckId_throwsNotFound() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(put("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), 0L)
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void getFavorites() throws Exception {
        Deck favorite = givenFavorite();
        User user = favorite.getCreatedBy();

        mvc.perform(get("/api/v1/users/{userId}/favorites", user.getId())
            .queryParam("offset", "0")
            .queryParam("limit", "10")
            .with(login(user.getAuthId())))
            .andExpect(status().isOk())
            .andExpect((jsonPath("content[0].id").value(favorite.getId())))
            .andExpect((jsonPath("content[0].name").value(favorite.getName())))
            .andExpect(jsonPath("numberOfElements").value(1))
            .andExpect(jsonPath("totalElements").value(1))
            .andExpect(jsonPath("totalPages").value(1))
            .andExpect(jsonPath("first").value(true))
            .andExpect(jsonPath("last").value(true))
            .andExpect(jsonPath("pageable.pageNumber").value(0))
            .andExpect(jsonPath("pageable.pageSize").value(10));
    }

    @Test
    public void getFavorites_withNoAuthentication_throwsForbidden() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(get("/api/v1/users/{userId}/favorites", user.getId())
            .queryParam("limit", "10")
            .queryParam("offset", "0"))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getFavorites_forOtherUser_throwsForbidden() throws Exception {
        User user = givenApplicationUser();
        Long otherUserId = user.getId() + 1;

        mvc.perform(get("/api/v1/users/{userId}/favorites", otherUserId)
            .queryParam("limit", "10")
            .queryParam("offset", "0")
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void hasFavorite_returnsNoContent() throws Exception {
        Deck favorite = givenFavorite();
        User user = favorite.getCreatedBy();

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), favorite.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNoContent());
    }

    @Test
    public void hasFavorite_whenNotFavorite_throwNotFound() throws Exception {
        Deck deck = givenDeck();
        User user = deck.getCreatedBy();

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void hasFavorite_whenUnknownDeck_throwNotFound() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), 0L)
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void hasFavorite_withNoAuthentication_throwsForbidden() throws Exception {
        Deck deck = givenDeck();
        User user = deck.getCreatedBy();

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void hasFavorite_forOtherUser_throwsForbidden() throws Exception {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Long otherUserId = user.getId() + 1;

        mvc.perform(get("/api/v1/users/{userId}/favorites/{deckId}", otherUserId, deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void removeFavorite() throws Exception {
        Deck favorite = givenFavorite();
        User user = favorite.getCreatedBy();

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), favorite.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isOk());
    }

    @Test
    public void removeFavorite_withUnknownDeckId_throwsNotFound() throws Exception {
        User user = givenApplicationUser();

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), 0L)
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void removeFavorite_withNoFavorite_throwsNotFound() throws Exception {
        User user = givenApplicationUser();
        Deck deck = givenDeck();

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), deck.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void removeFavorite_withNoAuthentication_throwsForbidden() throws Exception {
        Deck favorite = givenFavorite();
        User user = favorite.getCreatedBy();

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", user.getId(), favorite.getId()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void removeFavorite_forOtherUser_throwsForbidden() throws Exception {
        Deck favorite = givenFavorite();
        User user = favorite.getCreatedBy();
        Long otherUserId = user.getId() + 1;

        mvc.perform(delete("/api/v1/users/{userId}/favorites/{deckId}", otherUserId, favorite.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
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

        mvc.perform(delete("/api/v1/users/{userId}", user2.getId())
            .with(login(user1.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminDeleteAdmin_forbidden() throws Exception {
        User admin1 = givenApplicationUser();
        admin1.setAdmin(true);
        User admin2 = givenApplicationUser();
        admin2.setAdmin(true);

        mvc.perform(delete("/api/v1/users/{userId}", admin2.getId())
            .with(login(admin1.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminDeleteNonExistentUser_notFound() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);

        mvc.perform(delete("/api/v1/users/{userId}", admin.getId() + 1)
            .with(login(admin.getAuthId())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void adminDeleteUser() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);
        Progress progress = givenProgress();
        User user = progress.getId().getUser();

        mvc.perform(delete("/api/v1/users/{userId}", user.getId())
            .with(login(admin.getAuthId())))
            .andExpect(status().isNoContent());

        assertFalse(user.isEnabled());
        assertEquals("[deleted]", user.getUsername());
        assertEquals("[removed]", user.getDescription());
    }

    private static String longestCommonSubstring(String S1, String S2)
    {
        int Start = 0;
        int Max = 0;
        for (int i = 0; i < S1.length(); i++)
        {
            for (int j = 0; j < S2.length(); j++)
            {
                int x = 0;
                while (S1.charAt(i + x) == S2.charAt(j + x))
                {
                    x++;
                    if (((i + x) >= S1.length()) || ((j + x) >= S2.length())) break;
                }
                if (x > Max)
                {
                    Max = x;
                    Start = i;
                }
            }
        }
        return S1.substring(Start, (Start + Max));
    }
}
