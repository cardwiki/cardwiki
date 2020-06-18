package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class RevisionEditRepositoryTest extends TestDataGenerator {

    @Autowired
    private RevisionRepository revisionRepository;

    @Test
    public void givenRevision_whenSaveRevisionEdit_thenFindByIdReturnsRevisionEdit() {
        // When
        Agent persistentAgent = persistentAgent();

        RevisionEdit edit = persistentAgent.unpersist().editCard(persistentAgent.createCardIn(persistentAgent.createDeck()));
        edit = revisionRepository.saveAndFlush(edit);

        // Then
        assertEquals(edit, revisionRepository.findById(edit.getId()).orElseThrow());
    }

    @Test
    public void givenRevision_whenSaveRevisionEditWithTooLongText_thenFindByIdReturnsRevisionEdit() {
        // When
        Agent user = transientAgent();
        RevisionEdit edit = user.editCard(user.createCardIn(user.createDeck()));
        edit.setTextFront("x".repeat(RevisionEdit.MAX_TEXT_SIZE + 1));
        edit.setTextBack("back text");

        // Then
        assertThrows(ConstraintViolationException.class, () -> revisionRepository.saveAndFlush(edit));
    }

    @Test
    public void givenRevision_whenSaveRevisionEditWithBlankText_thenFindByIdReturnsRevisionEdit() {
        // When
        Agent user = transientAgent();
        RevisionEdit edit = user.editCard(user.createCardIn(user.createDeck()));
        edit.setTextFront("  ");

        // Then
        assertThrows(ConstraintViolationException.class, () -> revisionRepository.saveAndFlush(edit));
    }

    @Test
    public void givenRevision_whenSaveRevisionEditWithSpecialCharacters_thenFindByIdReturnsWithSpecialCharacters() {
        // When
        Agent user = transientAgent();
        RevisionEdit edit = user.editCard(user.createCardIn(user.createDeck()));
        edit.setTextFront(UTF_16_SAMPLE_TEXT);
        edit = revisionRepository.saveAndFlush(edit);

        // Then
        assertEquals(edit.getTextFront(), UTF_16_SAMPLE_TEXT);
    }

    @Test
    public void givenRevisionEdit_whenDeleteRevisionById_thenExistsByIdReturnsFalse() {
        Agent user = persistentAgent();
        RevisionEdit revisionEdit = user.editCard(user.createCardIn(user.createDeck()));

        // When
        revisionRepository.deleteById(revisionEdit.getId());
        revisionRepository.flush();

        // Then
        assertFalse(revisionRepository.existsById(revisionEdit.getId()));
    }
}
