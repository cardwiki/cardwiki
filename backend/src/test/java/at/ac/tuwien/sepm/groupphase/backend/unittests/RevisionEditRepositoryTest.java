package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionEditRepository;
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
    private RevisionEditRepository revisionEditRepository;

    private static final String UTF_16_SAMPLE_TEXT = "ユ简크로أفضل البحوثΣὲ γνДесแผ∮E⋅∞∑çéèñé";

    @Test
    public void givenNothing_whenSaveRevisionEditWithoutRevision_throwsJpaSystemException() {
        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront("front text");
        edit.setTextBack("back text");

        assertThrows(JpaSystemException.class, () -> revisionEditRepository.save(edit));
    }

    @Test
    public void givenRevision_whenSaveRevisionEdit_thenFindByIdReturnsRevisionEdit() {
        Revision revision = givenRevision();

        // When
        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront("front text");
        edit.setTextBack("back text");
        edit.setRevision(revision);
        edit = revisionEditRepository.saveAndFlush(edit);

        // Then
        assertEquals(edit, revisionEditRepository.findById(edit.getId()).orElseThrow());
    }

    @Test
    public void givenRevision_whenSaveRevisionEditWithTooLongText_thenFindByIdReturnsRevisionEdit() {
        Revision revision = givenRevision();

        // When
        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront("x".repeat(RevisionEdit.MAX_TEXT_SIZE + 1));
        edit.setTextBack("back text");
        edit.setRevision(revision);

        // Then
        assertThrows(ConstraintViolationException.class, () -> revisionEditRepository.saveAndFlush(edit));
    }

    @Test
    public void givenRevision_whenSaveRevisionEditWithBlankText_thenFindByIdReturnsRevisionEdit() {
        Revision revision = givenRevision();

        // When
        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront("  ");
        edit.setTextBack("back text");
        edit.setRevision(revision);

        // Then
        assertThrows(ConstraintViolationException.class, () -> revisionEditRepository.saveAndFlush(edit));
    }

    @Test
    public void givenRevision_whenSaveRevisionEditWithSpecialCharacters_thenFindByIdReturnsWithSpecialCharacters() {
        Revision revision = givenRevision();

        // When
        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront(UTF_16_SAMPLE_TEXT);
        edit.setTextBack("back text");
        edit.setRevision(revision);
        edit = revisionEditRepository.saveAndFlush(edit);

        // Then
        assertEquals(edit.getTextFront(), UTF_16_SAMPLE_TEXT);
    }
}
