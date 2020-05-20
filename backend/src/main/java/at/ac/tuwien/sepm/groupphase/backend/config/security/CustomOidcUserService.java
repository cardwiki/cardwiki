package at.ac.tuwien.sepm.groupphase.backend.config.security;

import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private SecurityConfig config;

    @Autowired
    private UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(userRequest);

        return new OidcUser() {
            @Override
            public Map<String, Object> getClaims() {
                throw new UnsupportedOperationException();
            }

            @Override
            public OidcUserInfo getUserInfo() {
                throw new UnsupportedOperationException();
            }

            @Override
            public OidcIdToken getIdToken() {
                throw new UnsupportedOperationException();
            }

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
