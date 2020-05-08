package at.ac.tuwien.sepm.groupphase.backend.config;

import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomOAuth2UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomOidcUserService customOidcUserService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable()
            .exceptionHandling().defaultAuthenticationEntryPointFor(new Http403ForbiddenEntryPoint(), new AntPathRequestMatcher("/**")).and()
            .oauth2Login()
                .userInfoEndpoint()
                    .userService(customOAuth2UserService)
                    .oidcUserService(customOidcUserService).and()
                .successHandler(new RefererRedirectionAuthenticationSuccessHandler());
    }

    public class RefererRedirectionAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

        public RefererRedirectionAuthenticationSuccessHandler() {
            super();
            setUseReferer(true);
        }
    }

    private final List<String> frontendOrigins = Collections.unmodifiableList(Arrays.asList("http://localhost:4200"));

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                final CorsConfiguration config = new CorsConfiguration();
                config.setAllowedMethods(Arrays.asList("*"));
                config.addAllowedHeader("*");

                // We allow credentials only for whitelisted frontends.
                if (frontendOrigins.contains(request.getHeader("Origin"))){
                    config.setAllowedOrigins(frontendOrigins);
                    config.setAllowCredentials(true);
                } else {
                    config.addAllowedOrigin("*");
                }
                return config;
            }
        };
    }

    @Bean
    public CorsFilter logoutCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(frontendOrigins);
        config.addAllowedMethod("POST");
        source.registerCorsConfiguration("/logout", config);
        return new CorsFilter(source);
    }
}
