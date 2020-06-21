package at.ac.tuwien.sepm.groupphase.backend.profiles.makeadmin;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
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

    private final UserRepository userRepository;

    @Autowired
    public AdminCreator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void makeAdmin() {
        userRepository.findByUsername(username).ifPresentOrElse(
            x -> {
                x.setAdmin(true);
                userRepository.save(x);
            },
            () -> {
                throw new UserNotFoundException("Could not find user '" + username + "'");
            }
        );
    }
}
