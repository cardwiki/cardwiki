package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
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
    @Transactional
    public User editSettings(long id, User user) {
        if (id != loadCurrentUser().getId()) throw new UserNotFoundException(); //TODO throw correct error

        User currentUser = userRepository.getOne(id);
        currentUser.setDescription(user.getDescription());

        return userRepository.save(currentUser);
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
    public Optional<User> loadUserByAuthId(String authId) {
        LOGGER.debug("Load user by AuthId {}",  authId);
        return userRepository.findByAuthId(authId);
    }

    @Override
    public User loadCurrentUser() {
        LOGGER.debug("Load current user");
        return loadUserByAuthId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause().getClass() == ConstraintViolationException.class) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                // TODO: change contains to equals after hibernate version contains
                //  https://github.com/hibernate/hibernate-orm/pull/3417
                if (cve.getConstraintName().contains(User.CONSTRAINT_USERNAME_UNIQUE))
                    throw new ConflictException("username already registered");
                else if (cve.getConstraintName().contains(User.CONSTRAINT_AUTHID_UNIQUE))
                    throw new ConflictException("authId already registered");
            }
            throw e;
        }
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

    @Override
    public User updateUser(Long id, User user) {
        return userRepository.saveAndFlush(user);
    }
}
