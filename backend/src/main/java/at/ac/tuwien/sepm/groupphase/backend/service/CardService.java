package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionCreate;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;


import java.util.List;

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
     */
    Card findOne(Long cardId);


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
}
