package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthEndpointTest extends TestDataGenerator {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getProviders() throws Exception {
        mvc.perform(get("/api/v1/auth/providers"))
            .andExpect(status().is(200));
        // TODO: test more thoroughly
    }

    @Test
    public void whoAmIAnon() throws Exception {
        mvc.perform(
            get("/api/v1/auth/whoami")
        )
        .andExpect(status().is(200))
        .andExpect(jsonPath("$.authId").value(IsNull.nullValue()))
        .andExpect(jsonPath("$.hasAccount").value(false))
        .andExpect(jsonPath("$.admin").value(false))
        .andExpect(jsonPath("$.id").value(IsNull.nullValue()));
    }

    @Test
    public void whoAmIUser() throws Exception {
        User user = validUser("gustav");
        persist(user);

        mvc.perform(
            get("/api/v1/auth/whoami")
            .with(login(user.getAuthId()))
        )
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.authId").value(user.getAuthId()))
            .andExpect(jsonPath("$.hasAccount").value(true))
            .andExpect(jsonPath("$.admin").value(false))
            .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    public void whoAmIAdmin() throws Exception {
        User user = validUser("gustav");
        user.setAdmin(true);
        persist(user);

        mvc.perform(
            get("/api/v1/auth/whoami")
                .with(login(user.getAuthId()))
        )
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.authId").value(user.getAuthId()))
            .andExpect(jsonPath("$.hasAccount").value(true))
            .andExpect(jsonPath("$.admin").value(true))
            .andExpect(jsonPath("$.id").value(user.getId()));
    }
}
