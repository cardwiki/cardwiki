package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RevisionRepository extends JpaRepository<Revision, Long> {
    /**
     * Find all revisions created by user
     *
     * @param id of the user to query
     * @param pageable pagination parameters for the query
     * @return ordered list of all revisions created by user
     */
    @EntityGraph(attributePaths = {"card", "card.deck"})
    Page<Revision> findByCreatedBy_Id(long id, Pageable pageable);
}
