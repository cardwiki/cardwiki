package at.ac.tuwien.sepm.groupphase.backend.profiles.dev;

import at.ac.tuwien.sepm.groupphase.backend.config.security.SecurityConfig;
import at.ac.tuwien.sepm.groupphase.backend.config.security.SecurityProps;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Profile("dev")
@RestController
@RequestMapping(value = "/api/v1/auth/providers/fake")
public class DevAuthProviderEndpoint {
    @Autowired
    private SecurityProps securityProps;

    @GetMapping("/{username}")
    public RedirectView get(@PathVariable String username, HttpServletRequest request, HttpServletResponse response){
        SecurityConfig.addTokenCookie(username, securityProps, request, response);
        return new RedirectView(SecurityConfig.FRONTEND);
    }
}

