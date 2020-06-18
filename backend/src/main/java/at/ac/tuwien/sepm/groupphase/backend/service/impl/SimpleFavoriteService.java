package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.InsufficientAuthorizationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.FavoriteService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;

@Service
public class SimpleFavoriteService implements FavoriteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final DeckService deckService;
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;

    public SimpleFavoriteService(DeckService deckService, UserService userService, UserRepository userRepository, DeckRepository deckRepository) {
        this.userService = userService;
        this.deckService = deckService;
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
    }


    @Override
    @Transactional
    public Deck addFavorite(Long userId, Long deckId) {
        LOGGER.debug("Add deck {} as favorite of {}", deckId, userId);
        User user = userService.loadCurrentUser();
        if (!user.getId().equals(userId))
            throw new InsufficientAuthorizationException("Cannot add favorites for other users");

        Deck deck = deckService.findOne(deckId);
        if (!user.getFavorites().add(deck))
            throw new ConflictException("Deck already saved as favorite");
        userRepository.save(user);

        return deck;
    }

    @Override
    @Transactional
    public Page<Deck> getFavorites(Long userId, Pageable pageable) {
        LOGGER.debug("Get favorites for {} with paging {}", userId, pageable);
        User user = userService.loadCurrentUser();
        if (!user.getId().equals(userId))
            throw new InsufficientAuthorizationException("Cannot get favorites from other users");

        return deckRepository.findByFavoredById(userId, pageable);
    }

    @Override
    @Transactional
    public boolean hasFavorite(Long userId, Long deckId) {
        LOGGER.debug("Check if {} has a favorite {}", userId, deckId);
        User user = userService.loadCurrentUser();
        if (!user.getId().equals(userId))
            throw new InsufficientAuthorizationException("Cannot get favorites from other users");

        // Throw if deck does not exist
        deckService.findOne(deckId);

        return deckRepository.existsByIdAndFavoredById(deckId, userId);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long deckId) {
        LOGGER.debug("Remove deck {} from favorites of {}", deckId, userId);
        User user = userService.loadCurrentUser();
        if (!user.getId().equals(userId))
            throw new InsufficientAuthorizationException("Cannot remove favorites for other users");

        Deck deck = deckService.findOne(deckId);
        if (!user.getFavorites().remove(deck))
            throw new DeckNotFoundException(String.format("Deck with id %s is no favorite", deckId));

        userRepository.save(user);
    }
}
