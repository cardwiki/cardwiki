package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionCreate;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.exception.CardNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

public interface CardService {

    /**
     * Add a new card to an existing deck.
     * Current user must be registered.
     *
     * @param deckId id of the deck where it will be added
     * @param revision data of the new card
     * @return created card
     * @throws DeckNotFoundException if no deck with this id exists
     * @throws AuthenticationRequiredException if no authenticated user could be found
     */
    Card addCardToDeck(Long deckId, RevisionCreate revision);

    /**
     * Find a single card by id.
     *
     * @param cardId id of the card
     * @return the card entry
     * @throws CardNotFoundException if no card with this id exists
     */
    Card findOneOrThrow(Long cardId);


    /**
     * Edit a card in an existing deck
     *
     * @param revision new data of the card
     * @param cardId id of the card
     * @return edited card
     */
    Card editCardInDeck(Long cardId, RevisionEdit revision);

    /**
     * Get all cards for a specific deck
     *
     * @param deckId of the deck
     * @return list of cards of the deck
     */
    List<RevisionEdit> findLatestEditRevisionsByDeckId(Long deckId);

    /**
     * Add delete-revision to card
     *
     * @param cardId of the card to which the delete-revision will be added
     * @param revisionMessage optional message for the delete-revision
     * @return card with added delete-revision
     */
    void addDeleteRevisionToCard(Long cardId, String revisionMessage);

    /**
     * Delete a card.
     *
     * @param cardId of the card to delete
     * @throws CardNotFoundException if no card with {@code cardId} exists
     */
    void delete(Long cardId);

    /**
     * Loads revisions of a card
     *
     * @param id of the card
     * @param pageable pagination settings
     * @return Page of Revisions of the card
     */
    Page<Revision> getRevisionsOfCard(Long id, Pageable pageable);

    /**
     * Loads revisions by id
     *
     * @param ids of the revisions to return
     * @return List of Revisions of the card
     */
    List<Revision> getRevisionsByIds(Long[] ids);
}
