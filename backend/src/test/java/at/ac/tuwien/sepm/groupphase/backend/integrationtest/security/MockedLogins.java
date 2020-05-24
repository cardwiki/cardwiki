package at.ac.tuwien.sepm.groupphase.backend.integrationtest.security;

import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.Collection;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;

public abstract class MockedLogins {
    public final static Collection<GrantedAuthority> ANONYMOUS_ROLES = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
    public final static Collection<GrantedAuthority> USER_ROLES = AuthorityUtils.createAuthorityList("ROLE_USER");
    public final static Collection<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");

    public static SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor mockLogin(Collection<? extends GrantedAuthority> authorities, String authId){
        OAuth2User user = Mockito.mock(OAuth2User.class);
        Mockito.doReturn(authorities).when(user).getAuthorities();
        Mockito.doReturn(authId).when(user).getName();
        return oauth2Login().oauth2User(user);
    }
}
