package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MiscellaneousEndpointTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void invalidTokenIsUnauthorized() throws Exception {
        mvc.perform(
            get("/some-route")
                .header("Authorization", "Bearer totallyInvalid")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void swaggerDoc() throws Exception {
        mvc.perform(get("/swagger-ui.html")).andExpect(status().isOk());
    }

}
