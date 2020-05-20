package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    //@Query("select c from Card c where c.deck.id = ?1 and c.latestRevision.revisionEdit is not null")
    List<Card> findCardsByDeck_Id(Long deckId);

    /**
     * Find card using id and include revisionSet
     *
     * @param cardId of the card
     * @return card including revisionSet
     */
    @EntityGraph(attributePaths = {"deck", "revisions", "latestRevision"})
    Optional<Card> findDetailsById(Long cardId);

    /**
     * Find card using id
     *
     * @param cardId of the card
     * @return card
     */
    @EntityGraph(attributePaths = {"deck", "latestRevision"})
    Optional<Card> findSimpleById(Long cardId);
}
