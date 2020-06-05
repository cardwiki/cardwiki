package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DeckRepositoryCustom {

    /**
     * Create a copy of a specific deck.
     * @param deckId of the deck to copy
     * @param user creating the copy
     * @param deckCopy deck containing data needed for the copy to be created
     * @return id of the copy created
     */
    Long createDeckCopy(Long deckId, User user, Deck deckCopy);
}
