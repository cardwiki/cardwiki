package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleDeckService implements DeckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckRepository deckRepository;
    private final UserService userService;


    public SimpleDeckService(DeckRepository deckRepository, UserService userService) {
        this.deckRepository = deckRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public Deck findOne(Long id) {
        LOGGER.debug("Find deck with id {}", id);
        Optional<Deck> deck = deckRepository.findById(id);
        if (deck.isPresent()) {
            Hibernate.initialize(deck.get().getCreatedBy());
            Hibernate.initialize(deck.get().getCategories());
            return deck.get();
        }
        else throw new NotFoundException(String.format("Could not find card deck with id %s", id));
    }

    @Override
    public List<Deck> searchByName(String name, Pageable pageable) {
        LOGGER.debug("Search card decks for name {} {}", name, pageable);
        return deckRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional
    @Override
    public Deck create(Deck deck, String oAuthId) {
        LOGGER.debug("Create new deck {}", deck);
        deck.setCreatedBy(userService.loadUserByOauthId(oAuthId));
        return deckRepository.save(deck);
    }

    @Transactional
    @Override
    public Deck update(Long id, Deck deck) {
        LOGGER.debug("Update deck with id: {}", deck.getId());
        if (deckRepository.existsById(id)) {
            deck.setId(id);
            return deckRepository.save(deck);
        } else {
            throw new NotFoundException(String.format("Could not find deck with id %s", deck.getId()));
        }
    }
}
