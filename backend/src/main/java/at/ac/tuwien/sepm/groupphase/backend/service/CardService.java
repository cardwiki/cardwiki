package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionCreate;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.exception.CardNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Find newest RevisionEdits of a deck
     *
     * @param deckId of the deck
     * @param pageable config for pagination
     * @return page of revision edits
     */
    Page<RevisionEdit> findLatestEditRevisionsByDeckId(Long deckId, Pageable pageable);

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
}
