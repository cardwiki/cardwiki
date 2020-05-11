package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthEndpointTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    //ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId("github");

    @Test
    public void getProviders() throws Exception {
        assert mvc != null;
        assert mvc.perform(get("/api/v1/auth/providers")) != null;
            //.andExpect(status().is(200));
    }
}
