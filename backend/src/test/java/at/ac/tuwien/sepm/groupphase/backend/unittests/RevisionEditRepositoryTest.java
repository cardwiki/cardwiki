package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionEditRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class RevisionEditRepositoryTest extends TestDataGenerator {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private RevisionEditRepository revisionEditRepository;

    @Test
    public void givenNothing_whenSaveRevisionEdit_throwsInvalidDataAccessApiUsageException() {
        Revision revision = new Revision();
        revision.setId(12L);
        revision.setMessage(REVISION_MESSAGE);
        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront(FRONT_TEXT);
        edit.setTextBack(BACK_TEXT);
        edit.setRevision(revision);
        revision.setRevisionEdit(edit);

        assertThrows(InvalidDataAccessApiUsageException.class, () -> revisionEditRepository.save(edit));
    }

    @Test
    public void givenCardAndRevision_whenSaveRevisionEdit_thenFindByIdReturnsRevisionEdit() {
        Revision revision = givenRevision();
        Card card = revision.getCard();

        // When
        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront(FRONT_TEXT);
        edit.setTextBack(BACK_TEXT);

        revision.setRevisionEdit(edit);
        edit.setRevision(revision);

        card = cardRepository.save(card);
        edit = card.getLatestRevision().getRevisionEdit();

        // Then
        assertEquals(edit, revisionEditRepository.findById(edit.getId()).orElseThrow());
    }

    @Test
    public void givenRevisionEdit_whenDeleteRevisionById_thenExistsByIdReturnsFalse() {
        RevisionEdit revisionEdit = givenRevisionEdit();
        Revision revision = revisionEdit.getRevision();

        // When
        revisionRepository.deleteById(revision.getId());
        revisionRepository.flush();

        // Then
        assertFalse(revisionEditRepository.existsById(revisionEdit.getId()));
    }
}
