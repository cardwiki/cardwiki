package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Find a comment by id and fetch createdBy
     *
     * @param commentId id of the comment to fetch
     * @return optional comment with prefetched createdBy
     */
    @EntityGraph(attributePaths = {"createdBy"})
    Optional<Comment> findById(Long commentId);

    /**
     * Find page of comments for a deck
     *
     * @param deckId id of the deck with the comments
     * @param pageable pagination parameters for the query
     * @return page of comments from this deck
     */
    @EntityGraph(attributePaths = {"createdBy"})
    Page<Comment> findByDeckId(Long deckId, Pageable pageable);

    /**
     * Find all comments created by user for exporting data
     *
     * @param userId id of the user
     * @return all comments by user
     */
    @EntityGraph(attributePaths = {"deck"})
    Set<Comment> findExportByCreatedBy_Id(Long userId);
}
