package at.ac.tuwien.sepm.groupphase.backend.config.security;

import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.Cookie;
import java.lang.invoke.MethodHandles;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SecurityProps securityProps;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        staticConfigure(httpSecurity, userService, securityProps, objectMapper);
    }

    /**
     * Meant to be called from WebSecurityConfigurerAdapter.configure()
     * This method is static so that it can also be easily used from the SecurityTestConfig.
     */
    public static void staticConfigure(HttpSecurity httpSecurity, UserService userService, SecurityProps securityProps, ObjectMapper objectMapper) throws Exception {
        // stateless sessions scale better, are better for user privacy,
        // make sessions persist restarts and make the backend easier to test.
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Not needed because we are stateless:
        httpSecurity.logout().disable();
        httpSecurity.csrf().disable();

        // the API is open for all
        httpSecurity.cors().configurationSource(allowAllCors());

        // required for h2-console
        httpSecurity.headers().frameOptions().sameOrigin();

        // on authentication failure don't redirect to /login but return 403
        httpSecurity.exceptionHandling().defaultAuthenticationEntryPointFor(new Http403ForbiddenEntryPoint(), new AntPathRequestMatcher("/**"));

        // we let Spring Security handle OAuth / OpenID Connect
        httpSecurity.oauth2Login()
            .authorizationEndpoint()
                .baseUri("/api/v1/auth/providers")
                .authorizationRequestRepository(new HttpCookieOAuth2AuthorizationRequestRepository(objectMapper));

        // on success we pass a JWT token to the frontend
        httpSecurity.oauth2Login()
            .successHandler((request, response, authentication) -> {
                OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                String token = Jwts.builder()
                    // we prefix the clientRegistrationId to prevent dangerous name collisions
                    .setSubject(auth.getAuthorizedClientRegistrationId() + ":" + auth.getName())
                    .setExpiration(new Date(System.currentTimeMillis() + securityProps.getExpirationTime()))
                    .signWith(Keys.hmacShaKeyFor(securityProps.getSecret()), SignatureAlgorithm.HS512)
                    .compact();

                // We pass the token with a cookie so that it is not stored in the browser history.
                Cookie tokenCookie = new Cookie("token", token);
                tokenCookie.setPath("/");
                response.addCookie(tokenCookie);

                // TODO: support multiple frontends
                response.sendRedirect("http://localhost:4200/login?success");
            });

        // for every request we query the database for user roles
        // and update the thread-local SecurityContext accordingly
        httpSecurity.addFilterBefore(new TokenAuthenticationFilter(userService, securityProps), UsernamePasswordAuthenticationFilter.class);
    }

    public static CorsConfigurationSource allowAllCors() {
        return request -> {
            final CorsConfiguration config = new CorsConfiguration();
            config.setAllowedMethods(Arrays.asList("*"));
            config.addAllowedHeader("*");
            config.addAllowedOrigin("*");
            return config;
        };
    }
}
