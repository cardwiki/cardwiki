package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProgressRepository extends JpaRepository<Progress,Progress.Id> {

    void deleteById_UserId(Long userId);

    void deleteById_CardId(Long cardId);

    boolean existsById_CardId(Long cardId);
 
    /**
     * Find all progress entries of user for exporting data
     *
     * @param userId id of the user
     * @return all progress entries by user
     */
    @EntityGraph(attributePaths = {"id.card.latestRevision.card.deck"})
    Set<Progress> findExportById_UserId(Long userId);
}
