package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
}
