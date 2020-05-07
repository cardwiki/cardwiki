package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/oauth2/authorization")
public class OAuth2ProvidersEndpoint {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping
    /**
     * Returns the available Authentication Providers as a map: {id: displayName}.
     */
    public Map<String, String> index() {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
            .as(Iterable.class);
        if (type != ResolvableType.NONE &&
            ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }
        Map<String, String> map = new HashMap<>();
        clientRegistrations.forEach(clientRegistration -> {
            map.put(clientRegistration.getRegistrationId(), clientRegistration.getClientName());
        });
        return map;
    }
}
