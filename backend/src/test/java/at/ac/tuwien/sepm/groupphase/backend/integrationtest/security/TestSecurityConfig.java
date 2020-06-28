package at.ac.tuwien.sepm.groupphase.backend.integrationtest.security;

import at.ac.tuwien.sepm.groupphase.backend.config.security.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.security.SecurityProps;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnableWebSecurity
@Configuration
@ActiveProfiles("test")
@Order(-1)
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {
    public static final SecurityProps SECURITY_PROPS = new SecurityProps("CardWikiIsAmazingAndThisSecretIsTotallySuperSecureAndUnguessable", 60-000);

    @Autowired
    private UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SecurityConfig.staticConfigure(http, userService, SECURITY_PROPS);
        http.oauth2Login()
            .tokenEndpoint().accessTokenResponseClient(
            oAuth2AuthorizationCodeGrantRequest ->
                OAuth2AccessTokenResponse.withToken("access-token-1234")
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .expiresIn(60 * 1000)
                    .build());
    }
}
