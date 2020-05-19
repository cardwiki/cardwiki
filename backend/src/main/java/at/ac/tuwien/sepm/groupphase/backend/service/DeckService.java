package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeckService {

    /**
     * Find a single card deck by id.
     *
     * @param id the id of the deck entry
     * @return the deck entry
     */
    Deck findOne(Long id);

    /**
     * Find all card decks containing {@code name} (case insensitive)
     *
     * @param name the search string
     * @param pageable the paging parameters
     * @return ordered list of all decks with names containing {@code name}
     */
    List<Deck> searchByName(String name, Pageable pageable);

    /**
     * Create a new card deck.
     *
     * @param deck to create.
     * @param OAuthId the OAuthID of the user creating the card
     * @return the new card deck.
     */
    Deck create(Deck deck, String OAuthId);

    /**
     * Update a Deck
     *
     * @param id of the deck to update
     * @param deck to update
     * @return updated deck
     */
    Deck update(Long id, Deck deck);
}
