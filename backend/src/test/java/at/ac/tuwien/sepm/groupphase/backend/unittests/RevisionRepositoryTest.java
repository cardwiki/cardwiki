package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
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
    private CardRepository cardRepository;

    @Autowired
    private RevisionRepository revisionRepository;

    @Test
    public void givenNothing_whenSaveRevisionWithCardIsNull_throwsDataIntegrityViolationException() {
        Revision revision = new Revision();
        revision.setMessage(REVISION_MESSAGE);

        assertThrows(DataIntegrityViolationException.class, () -> revisionRepository.save(revision));
    }

    @Test
    public void givenCard_whenSaveRevision_thenFindByIdReturnsRevision() {
        Card card = givenCard();

        Revision revision = new Revision();
        revision.setMessage(REVISION_MESSAGE);
        card.setLatestRevision(revision);
        revision.setCard(card);

        revisionRepository.save(revision);
        assertEquals(revision, revisionRepository.findById(revision.getId()).orElseThrow());
    }

    @Test
    public void givenCard_whenSaveRevisionWithTooLongMessage_thenThrow() {
        Card card = givenCard();

        Revision revision = new Revision();
        revision.setMessage("x".repeat(Revision.MAX_MESSAGE_SIZE + 1));
        card.setLatestRevision(revision);
        revision.setCard(card);

        assertThrows(ConstraintViolationException.class, () -> revisionRepository.save(revision));
    }

    @Test
    public void givenRevision_whenDeleteById_thenExistsIsFalse() {
        Revision revision = givenRevision();

        revisionRepository.deleteById(revision.getId());

        assertAll(
            () -> assertEquals(0, revisionRepository.count()),
            () -> assertFalse(revisionRepository.existsById(revision.getId()))
        );
    }

    @Test
    public void givenRevision_whenSetCardNull_thenThrowData() {
        Revision revision = givenRevision();
        revision.setCard(null);

        assertThrows(DataIntegrityViolationException.class, () -> revisionRepository.saveAndFlush(revision));
    }

    @Test
    public void givenLatestRevision_whenDeleteById_thenLatestRevisionIsNull() {
        Revision revision = givenRevision();
        Card card = revision.getCard();

        // When
        revisionRepository.deleteById(card.getLatestRevision().getId());
        revisionRepository.flush();

        // Then
        assertNull(cardRepository.findById(card.getId()).orElseThrow().getLatestRevision());
    }
}
