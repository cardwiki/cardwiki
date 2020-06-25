package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, Long> {
    /**
     * Find all revisions created by user
     *
     * @param userId of the user to query
     * @param pageable pagination parameters for the query
     * @return ordered list of all revisions created by user
     */
    @EntityGraph(attributePaths = {"card", "card.deck"})
    Page<Revision> findByCreatedBy_Id(long userId, Pageable pageable);

    /**
     * Find all revisions of a card
     *
     * @param id of the card
     * @param pageable pagination parameters for the query
     * @return ordered list of all revisions of the card
     */
    @EntityGraph(attributePaths = {"createdBy"})
    Page<Revision> findByCard_Id(long id, Pageable pageable);

    /**
     * Find all revisions for a deck
     *
     * @param id of the deck
     * @param pageable pagination parameters for the query
     * @return ordered list of all revisions of the deck
     */
    @EntityGraph(attributePaths = {"createdBy"})
    Page<Revision> findByCard_Deck_Id(long id, Pageable pageable);

    /**
     * Find revisions by ids
     *
     * @param ids of revisions
     * @return requested revisions
     */
    @EntityGraph(attributePaths = {"createdBy"})
    List<Revision> findByIdIn(List<Long> ids);
}
