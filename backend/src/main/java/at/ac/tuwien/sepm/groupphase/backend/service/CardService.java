package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;

import java.util.List;

public interface CardService {

    /**
     * Add a new card to an existing deck.
     * Current user must be registered.
     *
     * @param deckId id of the deck where it will be added
     * @param revisionEdit data of the new card
     * @return created card
     * @throws DeckNotFoundException if no deck with this id exists
     * @throws UserNotFoundException if no authenticated user could be found
     */
    Card addCardToDeck(Long deckId, RevisionEdit revisionEdit);

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
     * @param revisionEdit new data of the card
     * @param cardId id of the card
     * @return edited card
     */
    Card editCardInDeck(Long cardId, RevisionEdit revisionEdit);

    /**
     * Get all cards for a specific deck
     *
     * @param deckId of the deck
     * @return list of cards of the deck
     */
    List<Card> findCardsByDeckId(Long deckId);

    /**
     * Add delete-revision to card
     *
     * @param cardId of the card to which the delete-revision will be added
     * @return card with added delete-revision
     */
    Card addDeleteRevisionToCard(Long cardId);
}
