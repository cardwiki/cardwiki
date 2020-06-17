package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.CommentNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.InsufficientAuthorizationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CommentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CommentService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CommentServiceTest extends TestDataGenerator {
    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private DeckService deckService;

    @Autowired
    private CommentService commentService;

    @Test
    public void givenDeckAndUser_whenAddCommentToDeck_thenReturnComment() {
        Long deckId = 0L;
        Comment comment = mock(Comment.class);
        User user = mock(User.class);
        Deck deck = mock(Deck.class);

        when(deckService.findOne(deckId)).thenReturn(deck);
        when(userService.loadCurrentUser()).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenAnswer(returnsFirstArg());

        Comment result = commentService.addCommentToDeck(deckId, comment);

        verify(comment).setDeck(deck);
        verify(comment).setCreatedBy(user);
        assertNotNull(result);
    }

    @Test
    public void givenUser_whenAddCommentToDeck_thenThrowDeckNotFoundException() {
        Long deckId = 0L;
        Comment comment = mock(Comment.class);
        User user = mock(User.class);

        when(deckService.findOne(deckId)).thenThrow(DeckNotFoundException.class);
        when(userService.loadCurrentUser()).thenReturn(user);

        assertThrows(DeckNotFoundException.class, () -> commentService.addCommentToDeck(deckId, comment));
    }

    @Test
    public void givenDeck_whenAddCommentToDeck_thenThrowAuthenticationRequiredException() {
        Long deckId = 0L;
        Comment comment = mock(Comment.class);
        Deck deck = mock(Deck.class);

        when(deckService.findOne(deckId)).thenReturn(deck);
        when(userService.loadCurrentUser()).thenThrow(AuthenticationRequiredException.class);

        assertThrows(AuthenticationRequiredException.class, () -> commentService.addCommentToDeck(deckId, comment));
    }

    @Test
    public void givenComment_whenFindOne_thenReturnComment() {
        Long commentId = 0L;
        Comment comment = mock(Comment.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertEquals(comment, commentService.findOne(commentId));
    }

    @Test
    public void givenNothing_whenFindOne_thenThrowCommentNotFound() {
        Long commentId = 0L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.findOne(commentId));
    }

    @Test
    public void givenCommentsPage_whenFindCommentsByDeckId_thenReturnPage() {
        Long deckId = 0L;
        Deck deck = mock(Deck.class);
        Pageable pageable = mock(Pageable.class);
        Page<Comment> commentPage = mock(Page.class);

        when(deckService.findOne(deckId)).thenReturn(deck);
        when(commentRepository.findByDeckIdOrderByCreatedAtDesc(deckId, pageable)).thenReturn(commentPage);

        assertEquals(commentPage, commentService.findCommentsByDeckId(deckId, pageable));
    }

    @Test
    public void givenNothing_whenFindCommentsByDeckId_thenThrowDeckNotFound() {
        Long deckId = 0L;
        Pageable pageable = mock(Pageable.class);

        when(deckService.findOne(deckId)).thenThrow(DeckNotFoundException.class);

        assertThrows(DeckNotFoundException.class, () -> commentService.findCommentsByDeckId(deckId, pageable));
    }

    @Test
    public void givenComment_whenEditCommentMessage_thenReturnComment() {
        Long commentId = 0L;
        String newMessage = "new message";
        Comment originalComment = mock(Comment.class);
        Comment newComment = new Comment();
        newComment.setMessage(newMessage);
        User user = mock(User.class);

        when(originalComment.getCreatedBy()).thenReturn(user);
        when(userService.loadCurrentUser()).thenReturn(user);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(originalComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(returnsFirstArg());

        Comment result = commentService.editComment(commentId, newComment);

        verify(originalComment).setMessage(newMessage);
        assertNotNull(result);
    }

    @Test
    public void givenComment_whenEditCommentWithoutMessage_thenReturnComment() {
        Long commentId = 0L;
        Comment originalComment = mock(Comment.class);
        Comment newComment = new Comment();
        User user = mock(User.class);

        when(originalComment.getCreatedBy()).thenReturn(user);
        when(userService.loadCurrentUser()).thenReturn(user);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(originalComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(returnsFirstArg());

        Comment result = commentService.editComment(commentId, newComment);

        verify(originalComment, never()).setMessage(anyString());
        assertNotNull(result);
    }

    @Test
    public void givenComment_whenEditCommentOfOtherUser_thenThrowInsufficientAuthorizationException() {
        Long commentId = 0L;
        Comment originalComment = mock(Comment.class);
        Comment newComment = mock(Comment.class);
        User creator = mock(User.class);
        User otherUser = mock(User.class);

        when(originalComment.getCreatedBy()).thenReturn(creator);
        when(userService.loadCurrentUser()).thenReturn(otherUser);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(originalComment));

        assertThrows(InsufficientAuthorizationException.class, () -> commentService.editComment(commentId, newComment));
    }

    @Test
    public void givenUser_whenEditComment_thenThrowCommentNotFoundException() {
        Long commentId = 0L;
        User user = mock(User.class);
        Comment comment = mock(Comment.class);

        when(userService.loadCurrentUser()).thenReturn(user);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.editComment(commentId, comment));
    }

    @Test
    public void givenComment_whenEditComment_thenThrowAuthenticationRequiredException() {
        Long commentId = 0L;
        Comment comment = mock(Comment.class);

        when(userService.loadCurrentUser()).thenThrow(AuthenticationRequiredException.class);

        assertThrows(AuthenticationRequiredException.class, () -> commentService.editComment(commentId, comment));
    }

    @Test
    public void givenComment_whenDeleteComment_thenCallRepositoryDeleteById() {
        Long commentId = 0L;
        Comment comment = mock(Comment.class);
        User user = mock(User.class);

        when(comment.getCreatedBy()).thenReturn(user);
        when(userService.loadCurrentUser()).thenReturn(user);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);

        assertDoesNotThrow(() -> commentService.deleteComment(commentId));
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    public void givenComment_whenDeleteCommentAsAdmin_thenCallRepositoryDeleteById() {
        Long commentId = 0L;
        Comment comment = mock(Comment.class);
        User creator = mock(User.class);
        User admin = mock(User.class);
        when(admin.isAdmin()).thenReturn(true);

        when(comment.getCreatedBy()).thenReturn(creator);
        when(userService.loadCurrentUser()).thenReturn(admin);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(commentId);

        assertDoesNotThrow(() -> commentService.deleteComment(commentId));
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    public void givenComment_whenDeleteCommentOfOtherUser_thenThrowInsufficientAuthorizationException() {
        Long commentId = 0L;
        Comment comment = mock(Comment.class);
        User creator = mock(User.class);
        User otherUser = mock(User.class);
        when(otherUser.isAdmin()).thenReturn(false);

        when(comment.getCreatedBy()).thenReturn(creator);
        when(userService.loadCurrentUser()).thenReturn(otherUser);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(InsufficientAuthorizationException.class, () -> commentService.deleteComment(commentId));
    }

    @Test
    public void givenUser_whenDeleteComment_thenThrowCommentNotFoundException() {
        Long commentId = 0L;
        User user = mock(User.class);

        when(userService.loadCurrentUser()).thenReturn(user);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId));
    }

    @Test
    public void givenNothing_whenDeleteComment_thenThrowAuthenticationRequiredException() {
        when(userService.loadCurrentUser()).thenThrow(AuthenticationRequiredException.class);

        assertThrows(AuthenticationRequiredException.class, () -> commentService.deleteComment(0L));
    }
}
