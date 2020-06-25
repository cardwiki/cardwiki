package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimpleDeckService implements DeckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckRepository deckRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CardRepository cardRepository;
    private final RevisionRepository revisionRepository;

    public SimpleDeckService(DeckRepository deckRepository, UserService userService, CategoryService categoryService,
                             CardRepository cardRepository, RevisionRepository revisionRepository) {
        this.deckRepository = deckRepository;
        this.userService = userService;
        this.categoryService = categoryService;
        this.cardRepository = cardRepository;
        this.revisionRepository = revisionRepository;
    }

    @Transactional
    @Override
    public Deck findOneOrThrow(Long id) {
        LOGGER.debug("Find deck with id {}", id);
        Objects.requireNonNull(id, "id argument must not be null");
        Optional<Deck> deck = deckRepository.findById(id);
        return deck.orElseThrow(() -> new DeckNotFoundException(String.format("Could not find card deck with id %s", id)));
    }

    @Override
    public Page<Deck> searchByName(String name, Pageable pageable) {
        LOGGER.debug("Search card decks for name {} {}", name, pageable);
        Objects.requireNonNull(name, "name argument must not be null");
        return deckRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional
    @Override
    public Deck create(Deck deck) {
        LOGGER.debug("Create new deck {}", deck);
        Objects.requireNonNull(deck, "deck argument must not be null");
        User user = userService.loadCurrentUserOrThrow();
        deck.setCreatedBy(user);
        deck.setCreatedBy(user);
        return deckRepository.save(deck);
    }

    @Transactional
    @Override
    public Deck update(Long id, Deck deckUpdate) {
        LOGGER.debug("Update deck with id: {}", id);
        Deck deck = findOneOrThrow(id);
        deck.setName(deckUpdate.getName());
        Set<Category> categories = deck.getCategories();

        if (deckUpdate.getCategories() != null) {
            //add deck to new categories
            for (Category category : deckUpdate.getCategories()) {
                category = categoryService.findOneOrThrow(category.getId());
                category.getDecks().add(deck);
                categories.remove(category);
            }
            //remove deck from removed categories
            for (Category category : categories) {
                category = categoryService.findOneOrThrow(category.getId());
                category.getDecks().remove(deck);
            }
        }

        return deckRepository.save(deck);
    }

    @Transactional
    @Override
    public Deck copy(Long id, Deck deckCopy) {
        LOGGER.debug("Copy deck with id: {}", id);
        User currentUser = userService.loadCurrentUserOrThrow();
        Deck source = findOneOrThrow(id);

        Deck deck = create(deckCopy);
        deck.setCategories(new HashSet<>(source.getCategories()));
        for (Category category : source.getCategories()) {
            category.getDecks().add(deck);
        }
        deckRepository.save(deck);
        List<Card> cards = cardRepository.findLatestEditRevisionsByDeck_Id(id).map(sourceRevision -> {
            Card card = new Card();
            card.setDeck(deck);
            RevisionCreate rev = new RevisionCreate();
            rev.setMessage(String.format("Copied from deck %s.", id));
            rev.setCreatedBy(currentUser);
            rev.setCard(card);
            rev.setTextFront(sourceRevision.getTextFront());
            rev.setTextBack(sourceRevision.getTextBack());
            rev.setImageFront(sourceRevision.getImageFront());
            rev.setImageBack(sourceRevision.getImageBack());
            card.setLatestRevision(rev);
            return card;
        }).collect(Collectors.toList());

        cardRepository.saveAll(cards);
        cardRepository.flush();
        deck.getCards().addAll(cards);
        return deck;
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Delete deck with id {}", id);
        try {
            deckRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DeckNotFoundException(String.format("Could not find card deck with id %d", id));
        }
	}

    public Page<Revision> getRevisions(Long id, Pageable pageable) {
        LOGGER.debug("Load {} revisions with offset {} from deck {}", pageable.getPageSize(), pageable.getOffset(), id);
        return revisionRepository.findByCard_Deck_Id(id, pageable);
    }
}
