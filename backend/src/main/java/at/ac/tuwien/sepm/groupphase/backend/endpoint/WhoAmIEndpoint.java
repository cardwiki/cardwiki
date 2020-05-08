package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.WhoAmIDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/whoami")
public class WhoAmIEndpoint {

    @GetMapping
    @ApiOperation(value = "Returns information about the currently logged in user.")
    public WhoAmIDto index(Authentication auth) {
        WhoAmIDto dto = new WhoAmIDto();
        if (auth != null) {
            dto.setId(auth.getName());
            dto.setHasAccount(auth.getAuthorities().contains("ROLE_USER"));
            dto.setAdmin(auth.getAuthorities().contains("ROLE_ADMIN"));
        }
        return dto;
    }
}
