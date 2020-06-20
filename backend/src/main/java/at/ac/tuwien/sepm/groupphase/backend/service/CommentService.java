package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import at.ac.tuwien.sepm.groupphase.backend.exception.CommentNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeckNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.InsufficientAuthorizationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    /**
     * Add a new comment to an existing deck
     *
     * @param deckId id of the deck where it will be added
     * @param comment data of the new comment
     * @return created comment
     * @throws DeckNotFoundException if no deck with this id exists
     * @throws AuthenticationRequiredException if no authenticated user could be found
     */
    Comment addCommentToDeck(Long deckId, Comment comment);

    /**
     * Find a single comment by id
     *
     * @param commentId id of the comment
     * @return the comment
     * @throws CommentNotFoundException if no comment with this id exists
     */
    Comment findOneOrThrow(Long commentId);

    /**
     * Get page of comments for a deck
     *
     * @param deckId of the deck
     * @param pageable pagination for the query
     * @return page of comments of this deck
     * @throws DeckNotFoundException if no deck with this id exists
     */
    Page<Comment> findCommentsByDeckId(Long deckId, Pageable pageable);

    /**
     * Update an existing comment
     *
     * @param commentId id of the comment to edit
     * @param comment new data of the comment
     * @return edited comment
     * @throws CommentNotFoundException if no comment with this id exists
     * @throws AuthenticationRequiredException if no authenticated user could be found
     * @throws InsufficientAuthorizationException if the comment does not belong to the logged in user
     */
    Comment editComment(Long commentId, Comment comment);

    /**
     * Delete a comment
     *
     * @param commentId id of the comment to delete
     * @throws CommentNotFoundException if no comment this this id exists
     * @throws AuthenticationRequiredException if no authenticated user could be found
     * @throws InsufficientAuthorizationException if the comment does not belong to the logged in user and user is no admin
     */
    void deleteComment(Long commentId);
}
