package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ProgressRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @Autowired
    private LearnService learnService;

    @Test
    public void givenNothing_whenFindNextNonExistentDeck_thenThrowIllegalArgument() {
        Mockito.when(deckRepository.existsById(any())).thenReturn(false);

        assertThrows(
            IllegalArgumentException.class,
            () -> learnService.findNextCardsByDeckId(1L, Pageable.unpaged())
        );
    }
}
