package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.BadRequestException;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ProgressRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class LearnServiceTest extends TestDataGenerator {
    @MockBean
    private ProgressRepository progressRepository;

    @MockBean
    private DeckRepository deckRepository;

    @MockBean
    private UserService userService;

    @Autowired
    private LearnService learnService;

    @Captor
    ArgumentCaptor<Progress> argumentCaptor;

    @Test
    public void givenNothing_whenFindNextNonExistentDeck_thenThrowBadRequestException() {
        Mockito.when(deckRepository.existsById(any())).thenReturn(false);

        assertThrows(
            BadRequestException.class,
            () -> learnService.findNextCards(1L, false, Pageable.unpaged())
        );
    }

    @Test
    public void givenNothing_whenEASY() {
        Mockito.when(progressRepository.findById(any())).thenReturn(Optional.empty());

        assertLearnResult(AttemptInputDto.Status.EASY, Progress.Status.REVIEWING, 4, 2.5);
    }

    @Test
    public void givenNothing_whenGOOD() {
        Mockito.when(progressRepository.findById(any())).thenReturn(Optional.empty());

        assertLearnResult(AttemptInputDto.Status.GOOD, Progress.Status.LEARNING, 10, 2.5);
    }

    @Test
    public void givenNothing_whenAGAIN() {
        Mockito.when(progressRepository.findById(any())).thenReturn(Optional.empty());

        assertLearnResult(AttemptInputDto.Status.AGAIN, Progress.Status.LEARNING, 1, 2.5);
    }

    @Test
    public void givenLearning_whenGOOD() {
        Progress progress = new Progress();
        progress.setStatus(Progress.Status.LEARNING);
        progress.setInterval(10);
        progress.setEasinessFactor(2.5);

        Mockito.when(progressRepository.findById(any())).thenReturn(Optional.of(progress));

        assertLearnResult(AttemptInputDto.Status.GOOD, Progress.Status.REVIEWING, 1, 2.5);
    }

    @Test
    public void givenReviewing_whenEASY() {
        Progress progress = new Progress();
        progress.setStatus(Progress.Status.REVIEWING);
        progress.setInterval(4);
        progress.setEasinessFactor(2.5);

        Mockito.when(progressRepository.findById(any())).thenReturn(Optional.of(progress));

        assertLearnResult(AttemptInputDto.Status.EASY, Progress.Status.REVIEWING, 14, 2.65);
    }

    @Test
    public void givenReviewing_whenGOOD() {
        Progress progress = new Progress();
        progress.setStatus(Progress.Status.REVIEWING);
        progress.setInterval(4);
        progress.setEasinessFactor(2.5);

        Mockito.when(progressRepository.findById(any())).thenReturn(Optional.of(progress));

        assertLearnResult(AttemptInputDto.Status.GOOD, Progress.Status.REVIEWING, 10, 2.5);
    }

    @Test
    public void givenReviewing_whenAGAIN() {
        Progress progress = new Progress();
        progress.setStatus(Progress.Status.REVIEWING);
        progress.setInterval(4);
        progress.setEasinessFactor(2.5);

        Mockito.when(progressRepository.findById(any())).thenReturn(Optional.of(progress));

        assertLearnResult(AttemptInputDto.Status.AGAIN, Progress.Status.REVIEWING, 1, 2.3);
    }

    private void assertLearnResult(AttemptInputDto.Status result, Progress.Status expectedStatus, int expectedInterval, double expectedEF) {
        User user = getSampleUser();
        AttemptInputDto attempt = getSampleAttempt();
        attempt.setStatus(result);

        Mockito.when(userService.loadCurrentUserOrThrow()).thenReturn(user);

        learnService.saveAttempt(attempt);

        Mockito.verify(progressRepository).saveAndFlush(argumentCaptor.capture());
        assertEquals(expectedStatus, argumentCaptor.getValue().getStatus());
        assertEquals(expectedInterval, argumentCaptor.getValue().getInterval());
        assertEquals(expectedEF, argumentCaptor.getValue().getEasinessFactor());
    }
}
