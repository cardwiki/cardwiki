package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // We need to construct our own OAuth2User because the user returned by super is immutable.
        return new DefaultOAuth2User(setupRoles(user), user.getAttributes(), userNameAttributeName);
    }

    /**
     * This method integrates Spring Security's OAuth2 with our persistence layer.
     */
    public Collection<GrantedAuthority> setupRoles(OAuth2User user){
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");

        try {
            ApplicationUser u = userService.loadUserByOauthId(user.getName());
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            if (u.isAdmin())
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } catch (NotFoundException e){
        }

        return authorities;
    }
}
