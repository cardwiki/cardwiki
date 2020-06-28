package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;

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

    /**
     * Delete a card deck.
     *
     * @param id of the deck to delete
     * @throws DeckNotFoundException if the specified deck does not exist
     */
    void delete(Long id);

    /**
     * Create data for csv-export.
     *
     * @param pw PrintWriter provided by response
     * @param deckId of the deck to export
     * @throws IOException if the CSVPrinter encounters an error.
     */
    void createCsvData(PrintWriter pw, Long deckId) throws IOException;

    /**
     * Create data for csv-export.
     *
     * @param deckId of the deck to export
     * @param file containing the data to add to the deck
     * @throws IOException if the CSVPrinter encounters an error.
     */
    Deck addCards(Long deckId, MultipartFile file) throws IOException;

    /**
     * Loads revisions of cards in the deck
     *
     * @param id of the deck
     * @param pageable pagination data consisting of LIMIT and OFFSET
     * @return List of Revisions of cards in the deck
     */
    Page<Revision> getRevisions(Long id, Pageable pageable);
}
