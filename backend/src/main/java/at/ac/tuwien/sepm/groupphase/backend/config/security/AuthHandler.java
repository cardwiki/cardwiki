package at.ac.tuwien.sepm.groupphase.backend.config.security;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

public abstract class AuthHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String buildAuthId(OAuth2AuthenticationToken token){
        return token.getAuthorizedClientRegistrationId() + ":" + token.getName();
    }

    /**
     * This method integrates Spring Security's OAuth2 with our persistence layer.
     */
    public static void updateAuthentication(UserService userService){
        LOGGER.info("updating auth");
        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

        try {
            User u = userService.loadUserByAuthId(buildAuthId(auth));
            // TODO: check u.isEnabled()
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            if (u.isAdmin())
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } catch (NotFoundException e){
        }

        SecurityContextHolder.getContext().setAuthentication(new OAuth2AuthenticationToken(auth.getPrincipal(), authorities, auth.getAuthorizedClientRegistrationId()));
    }
}
