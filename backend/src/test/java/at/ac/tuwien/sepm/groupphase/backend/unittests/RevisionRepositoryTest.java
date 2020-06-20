package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class RevisionRepositoryTest extends TestDataGenerator {

    @Autowired
    private RevisionRepository revisionRepository;

    private static final String REVISION_MESSAGE = "Test Revision";

    @Test
    public void givenUser_whenSaveRevisionWithoutCard_throwsDataIntegrityViolationException() {
        User user = givenApplicationUser();
        RevisionCreate revision = new RevisionCreate();
        revision.setTextFront("foo");
        revision.setTextBack("foo");
        revision.setMessage(REVISION_MESSAGE);
        revision.setCreatedBy(user);

        assertThrows(DataIntegrityViolationException.class, () -> revisionRepository.save(revision));
    }

    @Test
    public void givenCard_whenSaveRevisionWithoutUser_throwsDataIntegrityViolationException() {
        Card card = givenCard();
        RevisionCreate revision = new RevisionCreate();
        revision.setMessage(REVISION_MESSAGE);
        revision.setCard(card);
        revision.setTextFront("foo");
        revision.setTextBack("foo");

        assertThrows(DataIntegrityViolationException.class, () -> revisionRepository.save(revision));
    }

    @Test
    public void givenCardAndUser_whenSaveRevision_thenFindByIdReturnsRevision() {
        Card card = givenCard();
        User user = givenApplicationUser();

        RevisionEdit revision = new RevisionEdit();
        revision.setMessage(REVISION_MESSAGE);
        revision.setCard(card);
        revision.setCreatedBy(user);
        revision.setTextFront("foo");
        revision.setTextBack("foo");
        revisionRepository.save(revision);

        assertEquals(revision, revisionRepository.findById(revision.getId()).orElseThrow());
    }

    @Test
    public void givenCardAndUser_whenSaveRevisionWithTooLongMessage_thenThrowConstraintViolationException() {
        Card card = givenCard();
        User user = givenApplicationUser();

        RevisionEdit revision = new RevisionEdit();
        revision.setMessage("x".repeat(Revision.MAX_MESSAGE_SIZE + 1));
        revision.setCard(card);
        revision.setTextFront("foo");
        revision.setTextBack("foo");
        revision.setCreatedBy(user);

        assertThrows(ConstraintViolationException.class, () -> revisionRepository.save(revision));
    }

    @Test
    public void givenCardAndUser_whenSaveRevisionWithBlankMessage_thenThrowConstraintViolationException() {
        Card card = givenCard();
        User user = givenApplicationUser();

        RevisionEdit revision = new RevisionEdit();
        revision.setMessage("   ");
        revision.setCard(card);
        revision.setTextFront("foo");
        revision.setTextBack("foo");
        revision.setCreatedBy(user);

        assertThrows(ConstraintViolationException.class, () -> revisionRepository.save(revision));
    }

    @Test
    public void givenRevision_whenDeleteById_thenExistsIsFalse() {
        Revision revision = givenCreateRevision();

        revisionRepository.deleteById(revision.getId());

        assertAll(
            () -> assertEquals(0, revisionRepository.count()),
            () -> assertFalse(revisionRepository.existsById(revision.getId()))
        );
    }
}
