package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
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

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class RevisionRepositoryTest implements TestData {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private RevisionRepository revisionRepository;

    @Test
    public void givenNothing_whenSaveRevisionWithCardIsNull_throwsDataIntegrityViolationException() {
        Revision revision = new Revision();
        revision.setMessage("Test Revision");

        assertThrows(DataIntegrityViolationException.class, () -> revisionRepository.save(revision));
    }

    @Test
    public void givenCard_whenSaveRevision_thenFindByIdReturnsRevision() {
        Card card = new Card();
        cardRepository.save(card);

        Revision revision = new Revision();
        revision.setMessage("Test Revision");
        card.setLatestRevision(revision);
        revision.setCard(card);

        revisionRepository.save(revision);
        assertEquals(revision, revisionRepository.findById(revision.getId()).orElseThrow());
    }

    @Test
    public void givenRevision_whenDeleteById_thenExistsIsFalse() {
        Card card = new Card();
        cardRepository.save(card);
        Revision revision = new Revision();
        revision.setMessage("Test Revision");
        revision.setCard(card);
        card.getRevisions().add(revision);
        revisionRepository.saveAndFlush(revision);

        revisionRepository.deleteById(revision.getId());

        assertAll(
            () -> assertEquals(0, revisionRepository.count()),
            () -> assertFalse(revisionRepository.existsById(revision.getId()))
        );
    }

    @Test
    public void givenLatestRevision_whenDeleteById_thenLatestRevisionIsNull() {
        Card card = new Card();
        Revision revision = new Revision();
        revision.setMessage("Test Revision");
        revision.setCard(card);
        cardRepository.save(card);
        card.setLatestRevision(revision);
        card = cardRepository.saveAndFlush(card);

        // When
        revisionRepository.deleteById(card.getLatestRevision().getId());
        revisionRepository.flush();

        // Then
        assertNull(cardRepository.findById(card.getId()).orElseThrow().getLatestRevision());
    }
}
