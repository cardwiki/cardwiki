package at.ac.tuwien.sepm.groupphase.backend.config.security;

import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserService userService;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        staticConfigure(httpSecurity);
        httpSecurity.oauth2Login()
            .successHandler((request, response, authentication) -> {
                AuthHandler.updateAuthentication(userService);
                // TODO: don't hardcode frontend
                response.sendRedirect("http://localhost:4200/login?success");
            });
    }

    public static void staticConfigure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors();
        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().sameOrigin(); // for h2-console
        httpSecurity.exceptionHandling().defaultAuthenticationEntryPointFor(new Http403ForbiddenEntryPoint(), new AntPathRequestMatcher("/**"));
        httpSecurity.logout().logoutUrl("/api/v1/auth/logout");
        httpSecurity.oauth2Login()
            .authorizationEndpoint().baseUri("/api/v1/auth/providers");
    }

    private final List<String> trustedOrigins = Collections.unmodifiableList(
        Arrays.asList(
            "http://localhost:4200", // our frontend
            "http://localhost:8080"  // our backend (for swagger-ui)
        ));

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                final CorsConfiguration config = new CorsConfiguration();
                config.setAllowedMethods(Arrays.asList("*"));
                config.addAllowedHeader("*");

                // We allow credentials only for whitelisted frontends.
                if (trustedOrigins.contains(request.getHeader("Origin"))){
                    config.setAllowedOrigins(trustedOrigins);
                    config.setAllowCredentials(true);
                } else {
                    config.addAllowedOrigin("*");
                }
                return config;
            }
        };
    }
}
