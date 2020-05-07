package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/whoami")
public class WhoAmIEndpoint {

    @GetMapping
    /**
     * Returns information about the currently logged in user.
     */
    public Map<String,Object> index(Authentication auth) {
        if (auth == null)
            throw new NotFoundException("please sign in");
        Map<String,Object> map = new HashMap<>();
        map.put("id", auth.getName());
        map.put("authorities", auth.getAuthorities().stream().map(o -> ((GrantedAuthority) o).getAuthority()));
        return map;
    }
}
