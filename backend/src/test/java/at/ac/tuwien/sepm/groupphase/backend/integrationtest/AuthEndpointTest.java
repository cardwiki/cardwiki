package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

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
public class AuthEndpointTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getProviders() throws Exception {
        mvc.perform(get("/api/v1/auth/providers"))
            .andExpect(status().is(200));
        // TODO: test more thoroughly
    }

    @Test
    public void testWhoAmIAnon() throws Exception {
        mvc.perform(
            get("/api/v1/auth/whoami")
        )
        .andExpect(status().is(200))
        .andExpect(jsonPath("$.id").value(IsNull.nullValue()))
        .andExpect(jsonPath("$.hasAccount").value(false))
        .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    public void testWhoAmIUser() throws Exception {
        mvc.perform(
            get("/api/v1/auth/whoami")
            .with(mockLogin(USER_ROLES, "foo"))
        )
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").value("foo"))
            .andExpect(jsonPath("$.hasAccount").value(true))
            .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    public void testWhoAmIAdmin() throws Exception {
        mvc.perform(
            get("/api/v1/auth/whoami")
                .with(mockLogin(ADMIN_ROLES, "foo"))
        )
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.id").value("foo"))
            .andExpect(jsonPath("$.hasAccount").value(true))
            .andExpect(jsonPath("$.admin").value(true));
    }
}
