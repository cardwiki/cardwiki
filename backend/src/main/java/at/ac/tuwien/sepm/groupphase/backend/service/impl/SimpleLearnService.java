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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

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
    public List<Card> findNextCardsByDeckId(Long deckId, Pageable pageable) {
        LOGGER.debug("Get next card for deck with id {}", deckId);
        return progressRepository.findNextCards(deckId, pageable);
    }

    private static final int[] LEARNING_STEPS = {1, 10}; // in minutes
    private static final int GRADUATING_INTERVAL = 1; // in days
    private static final int EASY_INTERVAL = 4; // in days
    public static final int EASY_BONUS = 130; // percentage

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
                    progress.setInterval(GRADUATING_INTERVAL);
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
                progress.setInterval(EASY_INTERVAL);
                progress.setStatus(Progress.Status.REVIEWING);
            }
        } else {
            switch (attempt.getStatus()) {
                case EASY: {
                    int ef = progress.getEasinessFactor() + 15;
                    progress.setEasinessFactor(Math.max(ef, Integer.MAX_VALUE));
                    long interval = ((long) progress.getInterval() * progress.getEasinessFactor() * (long) EASY_BONUS) / 10_000L;
                    try {
                        progress.setInterval(Math.toIntExact(interval));
                    } catch (ArithmeticException e) {
                        progress.setInterval(Integer.MAX_VALUE);
                    }
                    break;
                }
                case GOOD: {
                    long interval = ((long) progress.getInterval() * progress.getEasinessFactor()) / 100L;
                    try {
                        progress.setInterval(Math.toIntExact(interval));
                    } catch (ArithmeticException e) {
                        progress.setInterval(Integer.MAX_VALUE);
                    }
                    break;
                }
                case AGAIN: {
                    int ef = progress.getEasinessFactor() - 20;
                    progress.setEasinessFactor(Math.max(ef, 130));
                    long interval = (progress.getInterval() * 20L) / 100L;
                    try {
                        progress.setInterval(Math.toIntExact(interval));
                    } catch (ArithmeticException e) {
                        progress.setInterval(Integer.MAX_VALUE);
                    }
                    break;
                }
            }
        }

        progress.setDue(LocalDateTime.now().plusMinutes(progress.getInterval()));

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
