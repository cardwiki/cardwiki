package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
import org.springframework.stereotype.Service;

@Service
public class SimpleLearnService implements LearnService {
    @Override
    public Card findNextCardByDeckId(Long deckId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void saveAttempt(AttemptInputDto attempt) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
