package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OAuth2ProviderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.WhoAmIDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthEndpoint {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/providers")
    @ApiOperation(value = "Returns the available Authentication Providers.")
    public List<OAuth2ProviderDto> index() {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
            .as(Iterable.class);
        if (type != ResolvableType.NONE &&
            ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }
        return StreamSupport.stream(clientRegistrations.spliterator(), false)
            .map(c -> new OAuth2ProviderDto(c.getRegistrationId(), c.getClientName())).collect(Collectors.toList());
    }

    @GetMapping("/whoami")
    @ApiOperation(value = "Returns information about the currently logged in user.")
    public WhoAmIDto whoami(Authentication auth) {
        WhoAmIDto dto = new WhoAmIDto();
        if (auth != null) {
            dto.setAuthId(auth.getName());
            dto.setHasAccount(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
            dto.setAdmin(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
            dto.setUserId(userService.loadUserByAuthId(auth.getName()).map(User::getId).orElse(null));
        }
        return dto;
    }

    /* The following functions are handled by Spring Security,
    * we just define them here so that they show up in the documentation. */

    @GetMapping("/providers/{id}")
    @ApiOperation(value = "Redirects to the specified provider to log in")
    public void redirect(){
    }

    @PostMapping("/logout")
    @ApiOperation(value = "Logout the current user")
    public void logout(){
    }
}
