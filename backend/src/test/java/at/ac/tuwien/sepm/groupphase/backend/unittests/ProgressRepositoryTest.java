package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.ProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ProgressRepositoryTest extends TestDataGenerator {
    @Autowired
    private ProgressRepository progressRepository;

    @Test
    public void givenNewCard_whenFindNext_thenReturnCard() {
        User user = givenApplicationUser();
        Card card = givenCard();

        assertEquals(
            1,
            progressRepository.findNextCards(
                card.getDeck().getId(),
                user.getId(),
                Pageable.unpaged()
            ).size()
        );
    }

    @Test
    public void givenDueCard_whenFindNext_thenReturnCard() {
        Progress progress = givenProgress();
        progress.setDue(LocalDateTime.now().minusMinutes(1));

        assertEquals(
            1,
            progressRepository.findNextCards(
                progress.getId().getCard().getDeck().getId(),
                progress.getId().getUser().getId(),
                Pageable.unpaged()
            ).size()
        );
    }

    @Test
    public void givenCardNotDue_whenFindNext_thenReturnEmptyList() {
        Progress progress = givenProgress();
        progress.setDue(LocalDateTime.now().plusMinutes(1));

        assertEquals(
            0,
            progressRepository.findNextCards(
                progress.getId().getCard().getDeck().getId(),
                progress.getId().getUser().getId(),
                Pageable.unpaged()
            ).size()
        );
    }
}
