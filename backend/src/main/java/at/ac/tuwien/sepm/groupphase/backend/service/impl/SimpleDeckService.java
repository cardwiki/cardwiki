package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.*;

@Service
public class SimpleDeckService implements DeckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckRepository deckRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CardRepository cardRepository;

    @Autowired
    public SimpleDeckService(DeckRepository deckRepository, UserService userService, CategoryService categoryService,
                             CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.cardRepository = cardRepository;
    }

    @Transactional
    @Override
    public Deck findOne(Long id) {
        LOGGER.debug("Find deck with id {}", id);
        Objects.requireNonNull(id, "id argument must not be null");
        Optional<Deck> deck = deckRepository.findById(id);
        return deck.orElseThrow(() -> new DeckNotFoundException(String.format("Could not find card deck with id %s", id)));
    }

    @Override
    public List<Deck> searchByName(String name, Pageable pageable) {
        LOGGER.debug("Search card decks for name {} {}", name, pageable);
        Objects.requireNonNull(name, "name argument must not be null");
        return deckRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional
    @Override
    public Deck create(Deck deck) {
        LOGGER.debug("Create new deck {}", deck);
        Objects.requireNonNull(deck, "deck argument must not be null");
        deck.setCreatedBy(userService.loadCurrentUser());
        User user = userService.loadCurrentUser();
        if (user == null) throw new IllegalStateException("current user was null in secured api operation");
        deck.setCreatedBy(user);
        return deckRepository.save(deck);
    }

    @Transactional
    @Override
    public Deck update(Long id, Deck deckUpdate) {
        LOGGER.debug("Update deck with id: {}", id);
        Deck deck = findOne(id);
        deck.setName(deckUpdate.getName());
        Set<Category> categories = deck.getCategories();

        if (deckUpdate.getCategories() != null) {
            //add deck to new categories
            for (Category category : deckUpdate.getCategories()) {
                category = categoryService.findOneById(category.getId());
                category.getDecks().add(deck);
                categories.remove(category);
            }
            //remove deck from removed categories
            for (Category category : categories) {
                category = categoryService.findOneById(category.getId());
                category.getDecks().remove(deck);
            }
        }

        return deckRepository.save(deck);
    }

    @Transactional
    @Override
    public Deck copy(Long id, Deck newDeck) {
        LOGGER.debug("Copy deck with id: {}", id);
        List<RevisionEdit> revisionEdits = deckRepository.getRevisionEditsByDeckId(id);
        User currentUser = userService.loadCurrentUser();

        Deck deckCopy = new Deck();
        deckCopy.setName(newDeck.getName());
        deckCopy.setCreatedBy(currentUser);
        deckCopy = deckRepository.saveAndFlush(deckCopy);
        deckRepository.copyCategoriesOfDeck(id, deckCopy.getId());

        deckRepository.addCardCopiesToDeckCopy(deckCopy.getId(), revisionEdits , currentUser);

        return findOne(deckCopy.getId());
    }
}