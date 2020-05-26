package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress,Progress.Id> {
    @Query("select c from Card c left join Progress p on c.id = p.id.card.id and p.id.user.id = :userId where c.deck.id=:deckId order by p.status asc nulls first, p.due asc nulls first")
    List<Card> findNextCards(@Param("deckId") Long deckId, @Param("userId") Long userId, Pageable pageable);
}
