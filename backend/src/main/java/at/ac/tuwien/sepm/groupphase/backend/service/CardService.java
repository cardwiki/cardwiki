package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.springframework.security.core.Authentication;

import java.util.List;

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
     * Get all cards for a specific deck
     *
     * @param deckId of the deck whose cards to get
     * @return list of cards of the deck
     */
    List<Card> getCardsByDeckId(Long deckId);
}
