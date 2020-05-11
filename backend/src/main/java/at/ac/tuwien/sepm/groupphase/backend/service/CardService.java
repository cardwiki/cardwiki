package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.springframework.security.core.Authentication;

public interface CardService {

    /**
     * Add a new card to an existing deck
     *
     * @param revisionEdit data of the new card
     * @param deckId id of the deck where it will be added
     * @param oAuthId oauthid of the currently logged in user
     * @return created card
     */
    Card addCardToDeck(Long deckId, RevisionEdit revisionEdit, String oAuthId);

    /**
     * Find a single card by id.
     *
     * @param deckId id of the deck the card belongs to
     * @param cardId id of the card
     * @return the card entry
     */
    Card findOne(Long deckId, Long cardId);


    /**
     * Edit a card in an existing deck
     *
     * @param revisionEdit new data of the card
     * @param deckId id of the deck the card belongs to
     * @param cardId id of the card
     * @param oAuthId oauthid of the currently logged in user
     * @return edited card
     */
    Card editCardInDeck(Long deckId, Long cardId, RevisionEdit revisionEdit, String oAuthId);

}
