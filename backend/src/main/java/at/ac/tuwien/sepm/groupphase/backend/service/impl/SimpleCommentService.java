package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.CommentNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.InsufficientAuthorizationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CommentRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CommentService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;

@Service
public class SimpleCommentService implements CommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final DeckService deckService;
    private final CommentRepository commentRepository;

    public SimpleCommentService(CommentRepository commentRepository, DeckService deckService, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.deckService = deckService;
    }

    @Override
    @Transactional
    public Comment addCommentToDeck(Long deckId, Comment comment) {
        LOGGER.debug("add comment to deck {}: {}", deckId, comment);
        User user = userService.loadCurrentUserOrThrow();
        Deck deck = deckService.findOneOrThrow(deckId);

        comment.setCreatedBy(user);
        comment.setDeck(deck);

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment findOneOrThrow(Long commentId) {
        LOGGER.debug("find comment with id {}", commentId);
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(String.format("Could not find comment with id %s", commentId)));
    }

    @Override
    @Transactional
    public Page<Comment> findCommentsByDeckId(Long deckId, Pageable pageable) {
        LOGGER.debug("find comments by deckId {}: {}", deckId, pageable);
        deckService.findOneOrThrow(deckId);
        return commentRepository.findByDeckIdOrderByCreatedAtDesc(deckId, pageable);
    }

    @Override
    @Transactional
    public Comment editComment(Long commentId, Comment comment) {
        LOGGER.debug("edit comment with id {}: {}", commentId, comment);
        User user = userService.loadCurrentUserOrThrow();
        Comment originalComment = findOneOrThrow(commentId);

        if (originalComment.getCreatedBy() != user)
            throw new InsufficientAuthorizationException("Cannot edit comment of other user");

        originalComment.setMessage(comment.getMessage());

        return commentRepository.save(originalComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        LOGGER.debug("delete comment with id {}", commentId);
        User user = userService.loadCurrentUserOrThrow();
        Comment comment = findOneOrThrow(commentId);

        if (comment.getCreatedBy() != user && !user.isAdmin())
            throw new InsufficientAuthorizationException("Cannot delete comment of other user");

        commentRepository.deleteById(commentId);
    }
}
