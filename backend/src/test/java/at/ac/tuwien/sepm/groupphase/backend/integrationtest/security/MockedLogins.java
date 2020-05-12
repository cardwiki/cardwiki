package at.ac.tuwien.sepm.groupphase.backend.integrationtest.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;

public abstract class MockedLogins {
    public static SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor anonymousLogin(){
        return oauth2Login().authorities(AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    }

    public static SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor userLogin(){
        return oauth2Login().authorities(AuthorityUtils.createAuthorityList("ROLE_USER"));
    }

    public static SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor adminLogin(){
        return oauth2Login().authorities(AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER"));
    }
}
