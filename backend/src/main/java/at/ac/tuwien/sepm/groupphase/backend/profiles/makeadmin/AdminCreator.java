package at.ac.tuwien.sepm.groupphase.backend.profiles.makeadmin;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("makeAdmin")
public class AdminCreator {
    @Value("${admin-username}")
    private String username;

    @Autowired
    private UserService userService;

    @PostConstruct
    private void makeAdmin() {
        User user = userService.findUserByUsernameOrThrow(username);
        user.setAdmin(true);
        userService.updateUser(user.getId(), user);
    }
}
