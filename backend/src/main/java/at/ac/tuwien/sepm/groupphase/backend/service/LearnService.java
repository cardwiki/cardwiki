package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;

import java.util.List;

public interface LearnService {
    /**
     * @param deckId
     * @return the next card to learn
     */
    List<Card> findNextCardsByDeckId(Long deckId);

    /**
     * Save a new attempt.
     * @param attempt
     */
    void saveAttempt(AttemptInputDto attempt);
}
