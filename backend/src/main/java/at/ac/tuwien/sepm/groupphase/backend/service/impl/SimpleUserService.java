package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionRepository;
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
import java.util.Objects;

@Service
public class SimpleUserService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final RevisionRepository revisionRepository;

    @Autowired
    public SimpleUserService(UserRepository userRepository, DeckRepository deckRepository, RevisionRepository revisionRepository) {
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
        this.revisionRepository = revisionRepository;
    }

    @Override
    public List<Deck> getDecks(long id, Pageable pageable) {
        LOGGER.debug("Load {} decks with offset {} from user {}", pageable.getPageSize(), pageable.getOffset(), id);
        return deckRepository.findByCreatedBy_Id(id, pageable);
    }

    @Override
    public List<Revision> getRevisions(long id, Pageable pageable) {
        LOGGER.debug("Load {} revisions with offset {} from user {}", pageable.getPageSize(), pageable.getOffset(), id);
        return revisionRepository.findByCreatedBy_Id(id, pageable);
    }

    @Override
    public User editDescription(long id, String description) {
        User user = loadUserById(id);

        user.setDescription(description);

        return userRepository.save(user);
    }

    @Override
    public User loadUserById(long id) {
        LOGGER.debug("Load user by id {}", id);
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
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

    @Override
    public List<User> searchByUsername(String username, Pageable pageable) {
        LOGGER.debug("Search users for username {} {}", username, pageable);
        Objects.requireNonNull(username, "name argument must not be null");
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }
}
