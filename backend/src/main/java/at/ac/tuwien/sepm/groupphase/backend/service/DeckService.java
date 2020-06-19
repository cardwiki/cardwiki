package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeckService {

    /**
     * Find a single card deck by id.
     *
     * @param id the id of the deck entry
     * @return the deck entry
     * @throws DeckNotFoundException if the specified deck does not exist
     */
    Deck findOneOrThrow(Long id);

    /**
     * Find all card decks containing {@code name} (case insensitive)
     *
     * @param name the search string
     * @param pageable the paging parameters
     * @return ordered list of all decks with names containing {@code name}
     */
    Page<Deck> searchByName(String name, Pageable pageable);

    /**
     * Create a new card deck.
     *
     * @param deck to create.
     * @return the new card deck.
     * @throws AuthenticationRequiredException if no authenticated user could be found
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
     * @param deckCopy deck containing data needed for the deck to be created.
     * @return the new card deck.
     * @throws AuthenticationRequiredException if no authenticated user can be found
     * @throws DeckNotFoundException if no deck with the given id can be found
     */
    Deck copy(Long id, Deck deckCopy);
}
