package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LearnService {
    /**
     * @param deckId
     * @param reverse
     * @param pageable
     * @return next cards to learn
     */
    List<Card> findNextCards(Long deckId, boolean reverse, Pageable pageable);

    /**
     * Save a new attempt.
     * @param attempt
     */
    void saveAttempt(AttemptInputDto attempt);
}
