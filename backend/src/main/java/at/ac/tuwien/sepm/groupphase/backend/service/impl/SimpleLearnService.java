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

    @Override
    public void saveAttempt(AttemptInputDto attempt) {
        LOGGER.debug("Save new attempt {}", attempt);
        User user = userService.loadCurrentUser();
        Card card = new Card();
        card.setId(attempt.getCardId());

        Progress.Id id = new Progress.Id(user, card);

        Progress progress = progressRepository.findById(id).orElse(new Progress(id));
        // TODO: implement spaced repetition
        progress.setDue(LocalDateTime.now().plusHours(3));
        progress.setEasinessFactor(1);

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
