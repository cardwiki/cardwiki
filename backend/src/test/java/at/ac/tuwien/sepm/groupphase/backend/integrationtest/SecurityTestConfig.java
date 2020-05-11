package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EnableWebSecurity
@Configuration
@ActiveProfiles("test")
@Order(0)
public class SecurityTestConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login ->
                oauth2Login
                    .tokenEndpoint(tokenEndpoint ->
                        tokenEndpoint
                            .accessTokenResponseClient(this.mockAccessTokenResponseClient())
                    )
                    .userInfoEndpoint(userInfoEndpoint ->
                        userInfoEndpoint
                            .userService(this.mockUserService())
                    )
            );
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> mockAccessTokenResponseClient() {
        OAuth2AccessTokenResponse accessTokenResponse = OAuth2AccessTokenResponse.withToken("access-token-1234")
            .tokenType(OAuth2AccessToken.TokenType.BEARER)
            .expiresIn(60 * 1000)
            .build();

        OAuth2AccessTokenResponseClient tokenResponseClient = mock(OAuth2AccessTokenResponseClient.class);
        when(tokenResponseClient.getTokenResponse(any())).thenReturn(accessTokenResponse);
        return tokenResponseClient;
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> mockUserService() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", "joeg");
        attributes.put("first-name", "Joe");
        attributes.put("last-name", "Grandja");
        attributes.put("email", "joeg@springsecurity.io");

        GrantedAuthority authority = new OAuth2UserAuthority(attributes);
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(authority);

        DefaultOAuth2User user = new DefaultOAuth2User(authorities, attributes, "email");

        OAuth2UserService userService = mock(OAuth2UserService.class);
        when(userService.loadUser(any())).thenReturn(user);
        return userService;
    }

    @Bean
    OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }
}
