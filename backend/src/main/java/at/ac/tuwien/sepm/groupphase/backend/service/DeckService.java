package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;

public interface DeckService {
    /**
     * Find a single card deck by id.
     *
     * @param id the id of the deck entry
     * @return the deck entry
     */
    Deck findOne(Long id);
}
