package at.ac.tuwien.sepm.groupphase.backend.config.security;

import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true)
@Order(2)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SecurityProps securityProps;

    @Autowired
    private UserService userService;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        staticConfigure(httpSecurity, userService, securityProps);
    }

    public static final String FRONTEND = "http://localhost:4200/login?success";

    public static void addTokenCookie(String authId, SecurityProps securityProps, HttpServletRequest request, HttpServletResponse response){
        String token = Jwts.builder()
            .setSubject(authId)
            .setExpiration(new Date(System.currentTimeMillis() + securityProps.getExpirationTime()))
            .signWith(Keys.hmacShaKeyFor(securityProps.getSecret()), SignatureAlgorithm.HS512)
            .compact();
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setPath("/");
        tokenCookie.setSecure(request.getServletContext().getSessionCookieConfig().isSecure());
        response.addCookie(tokenCookie);
    }

    /**
     * Meant to be called from WebSecurityConfigurerAdapter.configure()
     * This method is static so that it can also be easily used from the SecurityTestConfig.
     */
    public static void staticConfigure(HttpSecurity httpSecurity, UserService userService, SecurityProps securityProps) throws Exception {
        // stateless sessions scale better, are better for user privacy,
        // make sessions persist restarts and make the backend easier to test.
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Not needed because we are stateless:
        httpSecurity.logout().disable();
        httpSecurity.csrf().disable();

        // the API is open for all
        httpSecurity.cors().configurationSource(allowAllCors());

        // lock down CSP
        httpSecurity.headers().addHeaderWriter(new StaticHeadersWriter("content-security-policy", "default-src 'none';"))
            .frameOptions().deny();

        // on authentication failure don't redirect to /login but return 403
        httpSecurity.exceptionHandling().defaultAuthenticationEntryPointFor(new Http403ForbiddenEntryPoint(), new AntPathRequestMatcher("/**"));

        // we let Spring Security handle OAuth / OpenID Connect
        httpSecurity.oauth2Login()
            .authorizationEndpoint()
                .baseUri("/api/v1/auth/providers")
                .authorizationRequestRepository(new HttpCookieOAuth2AuthorizationRequestRepository());

        // on success we pass a JWT token to the frontend
        httpSecurity.oauth2Login()
            .successHandler((request, response, authentication) -> {
                if (request.getServletContext().getSessionCookieConfig().isSecure() && !request.isSecure()){
                    // TODO: format error as JSON
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "HTTPS is required");
                    return;
                }

                OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

                // We pass the token with a cookie so that it is not stored in the browser history.
                // we prefix the clientRegistrationId to prevent dangerous name collisions
                addTokenCookie(auth.getAuthorizedClientRegistrationId() + ":" + auth.getName(), securityProps, request, response);

                // TODO: support multiple frontends
                response.sendRedirect(FRONTEND);
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

@Configuration
@Order(1)
class H2SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/h2-console/**");
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
    }
}

@Configuration
@Order(0)
class SwaggerUISecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/swagger-ui.html")
            .headers().addHeaderWriter(new StaticHeadersWriter("content-security-policy", "script-src 'self'; object-src 'none';"));
    }
}
