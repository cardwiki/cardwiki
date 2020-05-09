package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleDeckService implements DeckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckRepository deckRepository;

    @Autowired
    public SimpleDeckService(DeckRepository deckRepository) {
        this.deckRepository = deckRepository;
    }

    @Override
    public Deck findOne(Long id) {
        LOGGER.debug("Find deck with id {}", id);
        Optional<Deck> deck = deckRepository.findById(id);
        if (deck.isPresent()) return deck.get();
        else throw new NotFoundException(String.format("Could not find card deck with id %s", id));
    }

    @Override
    public List<Deck> searchByName(String name, Pageable pageable) {
        LOGGER.debug("Search card decks for name {} {}", name, pageable);
        return deckRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Deck create(Deck deck) {
        LOGGER.debug("Create new deck {}", deck);
        return deckRepository.save(deck);
    }

    @Override
    public Deck update(Deck deck) {
        LOGGER.debug("Update deck with id: {}", deck.getId());
        if (deckRepository.findById(deck.getId()).isPresent()) {
            return deckRepository.save(deck);
        } else {
            throw new NotFoundException(String.format("Could not find deck with id %s", deck.getId()));
        }
    }
}
