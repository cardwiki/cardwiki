package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class SimpleUserService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;

    @Autowired
    public SimpleUserService(UserRepository userRepository, DeckRepository deckRepository) {
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
    }

    @Override
    public List<Deck> getDecks(String username, Pageable pageable) {
        LOGGER.debug("Load {} decks with offset {} from user by username {}", pageable.getPageSize(), pageable.getOffset(), username);
        User user = loadUserByUsername(username);
        return deckRepository.findByCreatedBy(user, pageable);
    }

    @Override
    public User loadUserByUsername(String username) {
        LOGGER.debug("Load user by username {}", username);
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User loadUserByAuthId(String authId) {
        LOGGER.debug("Load user by AuthId {}",  authId);
        return userRepository.findByAuthId(authId).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User loadCurrentUser() {
        LOGGER.debug("Load current user");
        return loadUserByAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public User createUser(User user) {
        // TODO: only admins can create admin users
        // TODO: return proper error message if username is duplicate
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
