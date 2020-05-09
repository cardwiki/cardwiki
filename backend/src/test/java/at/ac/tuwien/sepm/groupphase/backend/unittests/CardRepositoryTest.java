package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class CardRepositoryTest implements TestData {

    @Autowired
    private CardRepository cardRepository;

    @Test
    public void givenNothing_whenSaveCard_thenExistsById() {
        Card card = new Card();
        cardRepository.save(card);

        assertTrue(cardRepository.existsById(card.getId()));
    }

    @Test
    public void givenNothing_whenSaveCardWithRevision_thenFindCardByIdHasLatestRevisionAndRevisionsIsNotEmpty() {
        Card card = new Card();
        Revision revision = new Revision();
        revision.setMessage("Test Revision");
        card.setLatestRevision(revision);
        revision.setCard(card);
        cardRepository.save(card);

        assertEquals(revision, cardRepository.findById(card.getId()).orElseThrow().getLatestRevision());
        assertFalse(cardRepository.findById(card.getId()).orElseThrow().getRevisions().isEmpty());
    }

    @Test
    public void givenCard_whenAddRevision_thenFindCardByIdHasRevision() {
        Card card = new Card();
        cardRepository.save(card);

        // When
        Revision revision = new Revision();
        revision.setMessage("Test Revision");
        card.setLatestRevision(revision);
        revision.setCard(card);
        cardRepository.saveAndFlush(card);

        // Then
        assertEquals(card.getLatestRevision(), cardRepository.findById(card.getId()).orElseThrow().getLatestRevision());
        assertFalse(cardRepository.findById(card.getId()).orElseThrow().getRevisions().isEmpty());
    }

    @Test
    public void givenCard_whenDeleteById_thenExistsByIdIsFalse() {
        Card card = new Card();
        cardRepository.save(card);

        cardRepository.deleteById(card.getId());

        assertFalse(cardRepository.existsById(card.getId()));
    }

    @Test
    public void givenCardAndRevisionEdit_whenDeleteById_thenNotExistsById() {
        Card card = new Card();
        Revision revision = new Revision();
        revision.setMessage("Test Revision");
        revision.setCard(card);
        card.setLatestRevision(revision);
        cardRepository.save(card);

        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront("Test Front");
        edit.setTextBack("Test Back");
        edit.setRevision(revision);
        revision.setRevisionEdit(edit);
        cardRepository.save(card);

        // When
        cardRepository.deleteById(card.getId());
        // Then
        assertFalse(cardRepository.existsById(card.getId()));
    }

    @Test
    public void givenCardAndRevisionEdit_whenDeleteRevision_thenRevisionsIsEmpty() {
        Card card = new Card();
        Revision revision = new Revision();
        revision.setMessage("Test Revision");
        revision.setCard(card);
        card.setLatestRevision(revision);
        cardRepository.save(card);

        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront("Test Front");
        edit.setTextBack("Test Back");
        edit.setRevision(revision);
        revision.setRevisionEdit(edit);
        card = cardRepository.save(card);

        // When
        card.setLatestRevision(null);
        card.getRevisions().remove(revision);
        card = cardRepository.save(card);

        // Then
        assertTrue(cardRepository.findById(card.getId()).orElseThrow().getRevisions().isEmpty());
    }
}
