package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;

public interface LearnService {
    /**
     * @param deckId
     * @return the next card to learn
     */
    Card findNextCardByDeckId(Long deckId);

    /**
     * Save a new attempt.
     * @param attempt
     */
    void saveAttempt(AttemptInputDto attempt);
}
