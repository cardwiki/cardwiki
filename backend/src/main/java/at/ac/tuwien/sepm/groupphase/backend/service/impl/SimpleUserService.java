package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SimpleUserService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;

    @Autowired
    public SimpleUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String username) {
        LOGGER.debug("Load user by username {}", username);
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User loadUserByAuthId(String authId) {
        LOGGER.debug("Load user by AuthId {}",  authId);
        return userRepository.findByAuthId( authId).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User loadCurrentUser() {
        LOGGER.debug("Load current user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return loadUserByAuthId(authentication.getName());
    }

    @Override
    public User createUser(User user) {
        // TODO: only admins can create admin users
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
