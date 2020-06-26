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
    @Modifying
    @Query("delete from Progress p where p.id.user.id=:userId")
    void deleteUserProgress(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Progress p where p.id.card.id=:cardId")
    void deleteCardProgress(@Param("cardId") Long cardId);

    @Query("select case when count(p) > 0 then true else false end from Progress p where p.id.card.id=:cardId")
    boolean existsCardWithProgress(@Param("cardId") Long cardId);

    /**
     * Find all progress entries of user for exporting data
     *
     * @param userId id of the user
     * @return all progress entries by user
     */
    @EntityGraph(attributePaths = {"id.card.latestRevision.card.deck"})
    Set<Progress> findExportById_UserId(Long userId);
}
