package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.BadRequestException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ProgressRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Service
public class SimpleLearnService implements LearnService {

    public static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ProgressRepository progressRepository;
    private final UserService userService;

    @Autowired
    public SimpleLearnService(ProgressRepository progressRepository, UserService userService) {
        this.progressRepository = progressRepository;
        this.userService = userService;
    }

    @Override
    public Card findNextCardByDeckId(Long deckId) {
        LOGGER.debug("Get next card for deck with id {}", deckId);
        throw new UnsupportedOperationException("not implemented yet");
    }

    private static final int LEARNING_STEPS[] = {1, 10};
    private static final int INITIAL_REVIEW_INTERVAL = 1;

    @Override
    public void saveAttempt(AttemptInputDto attempt) {
        LOGGER.debug("Save new attempt {}", attempt);
        User user = userService.loadCurrentUser();
        Card card = new Card();
        card.setId(attempt.getCardId());

        Progress.Id id = new Progress.Id(user, card);

        Progress progress = progressRepository.findById(id).orElse(new Progress(id));

        // This spaced-repetition algorithm is based on Anki's algorithm, which is based on SuperMemo 2.

        if (progress.getStatus() == Progress.Status.LEARNING){
            if (attempt.getStatus() == AttemptInputDto.Status.AGAIN){
                progress.setInterval(LEARNING_STEPS[0]);
            } else if (attempt.getStatus() == AttemptInputDto.Status.GOOD){
                if (progress.getInterval() >= LEARNING_STEPS[LEARNING_STEPS.length - 1]){
                    progress.setInterval(INITIAL_REVIEW_INTERVAL);
                    progress.setStatus(Progress.Status.REVIEWING);
                } else {
                    for (int min : LEARNING_STEPS){
                        if (min > progress.getInterval()){
                            progress.setInterval(min);
                            break;
                        }
                    }
                }
            } else if (attempt.getStatus() == AttemptInputDto.Status.EASY){
                progress.setInterval(INITIAL_REVIEW_INTERVAL);
                progress.setStatus(Progress.Status.REVIEWING);
            }

            progress.setDue(LocalDateTime.now().plusMinutes(progress.getInterval()));
        } else {
            // TODO: implement REVIEWING
        }

        try {
            progressRepository.saveAndFlush(progress);
        } catch (DataIntegrityViolationException e){
            if (e.getCause().getClass() == ConstraintViolationException.class){
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                // TODO: change contains to equals after hibernate version contains
                //  https://github.com/hibernate/hibernate-orm/pull/3417
                if (cve.getConstraintName().contains(Progress.FKNAME_CARD))
                    throw new BadRequestException("cardId not found");
                else
                    throw e;
            }
        }
    }
}
