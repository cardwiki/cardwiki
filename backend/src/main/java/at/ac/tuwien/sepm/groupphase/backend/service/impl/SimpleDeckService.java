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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SimpleDeckService implements DeckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckRepository deckRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CardRepository cardRepository;

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
    public Deck copy(Long id, Deck deckCopy) {
        LOGGER.debug("Copy deck with id: {}", id);
        User currentUser = userService.loadCurrentUser();
        Deck source = findOne(id);

        Deck deck = create(deckCopy);
        deck.setCategories(new HashSet<>(source.getCategories()));
        for (Category category : source.getCategories()) {
            category.getDecks().add(deck);
        }
        deckRepository.save(deck);

        List<Card> srcCards = cardRepository.findCardsWithContentByDeck_Id(id);
        List<Card> destCards = srcCards.stream()
            .map(srcCard -> {
                Card card = new Card();
                card.setDeck(deck);
                Revision revision = new Revision();
                revision.setMessage(String.format("Copied from deck %s.", id));
                revision.setCreatedBy(currentUser);
                revision.setCard(card);
                card.setLatestRevision(revision);
                return card;
            })
            .collect(Collectors.toList());
        cardRepository.saveAll(destCards);
        cardRepository.flush();

        IntStream
            .range(0, destCards.size())
            .forEach(i -> {
                RevisionEdit revisionEdit = new RevisionEdit();
                revisionEdit.setTextFront(srcCards.get(i).getLatestRevision().getRevisionEdit().getTextFront());
                revisionEdit.setTextBack(srcCards.get(i).getLatestRevision().getRevisionEdit().getTextBack());
                destCards.get(i).getLatestRevision().setRevisionEdit(revisionEdit);
                revisionEdit.setRevision(destCards.get(i).getLatestRevision());
            });
        cardRepository.saveAll(destCards);
        cardRepository.flush();
        deck.getCards().addAll(destCards);

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
}