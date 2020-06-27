package at.ac.tuwien.sepm.groupphase.backend.profiles.dev;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.AuthEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OAuth2ProviderDto;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@Order(2)
public class DeveloperLogin implements AuthEndpoint.OAuth2ProviderSupplier {

    public static final String ADMIN_NAME = "anja";
    public static final String USER_NAME = "bob";

    @Override
    public List<OAuth2ProviderDto> get() {
        // The slash in the authid is important:
        // /api/v1/auth/providers/{id} is handled by Spring Security's OAuth2AuthorizationRequestRedirectFilter
        // To override that filter we would need a WebSecurityConfigurerAdapter which is a bit cumbersome.
        // Instead we simple add a slash to the id making the spring filter ignore the request and letting us
        // handle it in a regular endpoint.

        return List.of(
            new OAuth2ProviderDto("fake/" + ADMIN_NAME, "Test Admin"),
            new OAuth2ProviderDto("fake/" + USER_NAME, "Test User")
        );
    }
}
