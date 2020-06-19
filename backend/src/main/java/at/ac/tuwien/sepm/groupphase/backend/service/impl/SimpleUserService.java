package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.InsufficientAuthorizationException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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
    public Page<Deck> getDecks(Long id, Pageable pageable) {
        LOGGER.debug("Load {} decks with offset {} from user {}", pageable.getPageSize(), pageable.getOffset(), id);
        return deckRepository.findByCreatedBy_Id(id, pageable);
    }

    @Override
    public Page<Revision> getRevisions(Long id, Pageable pageable) {
        LOGGER.debug("Load {} revisions with offset {} from user {}", pageable.getPageSize(), pageable.getOffset(), id);
        return revisionRepository.findByCreatedBy_Id(id, pageable);
    }

    @Override
    @Transactional
    public User editSettings(Long id, User user) {
        User currentUser = loadCurrentUserOrThrow();
        if (!id.equals(currentUser.getId()))
            throw new InsufficientAuthorizationException("Cannot edit settings for other users");

        currentUser.setDescription(user.getDescription());

        return userRepository.save(currentUser);
    }

    @Override
    public User findUserByIdOrThrow(Long id) {
        LOGGER.debug("Load user by id {}", id);
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User findUserByUsernameOrThrow(String username) {
        LOGGER.debug("Load user by username {}", username);
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Optional<User> findUserByAuthId(String authId) {
        LOGGER.debug("Load user by AuthId {}",  authId);
        return userRepository.findByAuthId(authId);
    }

    @Override
    public User loadCurrentUserOrThrow() {
        LOGGER.debug("Load current user");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null)
            throw new AuthenticationRequiredException("Cannot load active user because not authentication is given");
        return findUserByAuthId(auth.getName()).orElseThrow(() -> new AuthenticationRequiredException("No user with this authentication exists"));
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
    public Page<User> searchByUsername(String username, Pageable pageable) {
        LOGGER.debug("Search users for username {} {}", username, pageable);
        Objects.requireNonNull(username, "name argument must not be null");
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }

    @Override
    public User updateUser(Long id, User user) {
        return userRepository.saveAndFlush(user);
    }
}
