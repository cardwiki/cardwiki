package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    //@Query("select c from Card c where c.deck.id = ?1 and c.latestRevision.revisionEdit is not null")
    List<Card> findCardsByDeck_Id(Long deckId);
}
