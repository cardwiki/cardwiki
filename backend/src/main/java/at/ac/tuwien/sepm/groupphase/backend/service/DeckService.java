package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeckService {

    /**
     * Find a single card deck by id.
     *
     * @param id the id of the deck entry
     * @return the deck entry
     * @throws DeckNotFoundException if the specified deck does not exist
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
     * @return the new card deck.
     * @throws UserNotFoundException if no authenticated user could be found
     */
    Deck create(Deck deck);

    /**
     * Update a Deck
     *
     * @param id of the deck to update
     * @param deckUpdate to update
     * @return updated deck
     */
    Deck update(Long id, Deck deckUpdate);

    /**
     * Create a new card deck by copying an existing one.
     *
     * @param id of the deck to copy
     * @param newDeck to create.
     * @return the new card deck.
     * @throws UserNotFoundException if no authenticated user could be found
     */
    Deck copy(Long id, Deck newDeck);
}
