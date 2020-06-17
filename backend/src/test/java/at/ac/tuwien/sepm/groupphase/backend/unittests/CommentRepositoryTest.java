package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class CommentRepositoryTest extends TestDataGenerator {
    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void givenDeckAndUser_whenSaveComment_findOneByIdReturnsComment() {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Comment comment = new Comment();
        comment.setMessage("some message");
        comment.setCreatedBy(user);
        comment.setDeck(deck);

        assertEquals(comment, commentRepository.save(comment));
    }

    @Test
    public void givenDeckAndUser_whenSaveCommentWithBlankMessage_throwConstraintViolationException() {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Comment comment = new Comment();
        comment.setMessage("\t   \t");
        comment.setCreatedBy(user);
        comment.setDeck(deck);

        assertThrows(ConstraintViolationException.class, () -> commentRepository.save(comment));
    }

    @Test
    public void givenDeckAndUser_whenSaveCommentWithTooLongMessage_throwConstraintViolationException() {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Comment comment = new Comment();
        comment.setMessage("x".repeat(Comment.MAX_MESSAGE_LENGTH + 1));
        comment.setCreatedBy(user);
        comment.setDeck(deck);

        assertThrows(ConstraintViolationException.class, () -> commentRepository.save(comment));
    }

    @Test
    public void givenUser_whenSaveComment_throwDataIntegrityViolationException() {
        User user = givenApplicationUser();
        Comment comment = new Comment();
        comment.setMessage("some message");
        comment.setCreatedBy(user);

        assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment));
    }

    @Test
    public void givenDeck_whenSaveComment_throwDataIntegrityViolationException() {
        Deck deck = givenDeck();
        Comment comment = new Comment();
        comment.setMessage("some message");
        comment.setDeck(deck);

        assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment));
    }

    @Test
    public void givenComment_whenUpdateMessage_thenReturnNewComment() {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Comment comment = new Comment();
        comment.setMessage("original message");
        comment.setCreatedBy(user);
        comment.setDeck(deck);
        commentRepository.saveAndFlush(comment);

        String newMessage = "new message";
        comment.setMessage(newMessage);
        comment = commentRepository.saveAndFlush(comment);
        assertEquals(newMessage, comment.getMessage());
    }

    @Test
    public void givenComment_whenDeleteById_thenExistsByIdReturnsFalse() {
        Deck deck = givenDeck();
        User user = givenApplicationUser();
        Comment comment = new Comment();
        comment.setMessage("original message");
        comment.setCreatedBy(user);
        comment.setDeck(deck);
        commentRepository.saveAndFlush(comment);

        commentRepository.deleteById(comment.getId());
        assertFalse(commentRepository.existsById(comment.getId()));
    }

    @Test
    public void givenComments_whenfindByDeckIdOrderByCreatedAtDesc_thenReturnOrderedCommentsOfDeck() {
        Deck firstDeck = givenDeck();
        Deck secondDeck = givenDeck();
        User user = givenApplicationUser();

        Comment firstComment = new Comment();
        firstComment.setMessage("first comment");
        firstComment.setDeck(firstDeck);
        firstComment.setCreatedBy(user);
        commentRepository.saveAndFlush(firstComment);

        Comment secondComment = new Comment();
        secondComment.setMessage("second comment");
        secondComment.setDeck(secondDeck);
        secondComment.setCreatedBy(user);
        commentRepository.saveAndFlush(secondComment);

        Comment thirdComment = new Comment();
        thirdComment.setMessage("third comment");
        thirdComment.setDeck(firstDeck);
        thirdComment.setCreatedBy(user);
        commentRepository.saveAndFlush(thirdComment);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = commentRepository.findByDeckIdOrderByCreatedAtDesc(firstDeck.getId(), pageable);

        assertAll(
            () -> assertEquals(2, commentPage.getNumberOfElements(), "finds all comments from deck one"),
            () -> assertEquals(0, commentPage.getNumber(), "returns first page"),
            () -> assertTrue(commentPage.isLast(), "is last page")
        );

        assertAll(
            () -> assertEquals(thirdComment.getId(), commentPage.getContent().get(0).getId(), "first entry is newest comment"),
            () -> assertEquals(firstComment.getId(), commentPage.getContent().get(1).getId(), "second entry is oldest comment")
        );
    }
}
