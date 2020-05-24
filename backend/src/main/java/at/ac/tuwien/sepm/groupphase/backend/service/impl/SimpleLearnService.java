package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.ProgressRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimpleLearnService implements LearnService {
    @Autowired
    private UserService userService;

    @Autowired
    private ProgressRepository progressRepository;

    @Override
    public Card findNextCardByDeckId(Long deckId) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void saveAttempt(AttemptInputDto attempt) {
        User user = userService.loadCurrentUser();
        Card card = new Card();
        card.setId(attempt.getCardId());

        Progress progress = new Progress();
        progress.setId(new Progress.Id(user, card));
        progressRepository.saveAndFlush(progress);
    }
}
