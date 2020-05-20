package at.ac.tuwien.sepm.groupphase.backend.config.security;

import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private SecurityConfig config;

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        return new OAuth2User() {
            @Override
            public Map<String, Object> getAttributes() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return SecurityConfig.setupRoles(userService, getName());
            }

            @Override
            public String getName() {
                return SecurityConfig.buildAuthId(userRequest.getClientRegistration(), user.getName());
            }
        };
    }
}
