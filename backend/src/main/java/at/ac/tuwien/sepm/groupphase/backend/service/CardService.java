package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.springframework.security.core.Authentication;

public interface CardService {

    /**
     * Add a new card to an existing deck
     *
     * @param revisionEdit data of the new card
     * @param deckId id of the deck where it will be added
     * @param authentication currently logged in user
     * @return created card
     */
    Card addCardToDeck(Long deckId, RevisionEdit revisionEdit, Authentication authentication);

}
