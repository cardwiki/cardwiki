package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Find cards for a specific deck
     *
     * @param deckId of the deck
     * @return list of cards of the deck
     */
    @EntityGraph(attributePaths = {"latestRevision.revisionEdit.imageFront", "latestRevision.revisionEdit.imageBack"})
    List<Card> findCardsByDeck_Id(Long deckId);

    /**
     * Find cards of a specific deck, excludes currently empty ones
     *
     * @param deckId of the deck
     * @return list of cards of the deck, excluding currently empty ones
     */
    @Query(value="select c from Card c inner join RevisionEdit r on r.revision=c.latestRevision where c.deck.id=:deckId")
    List<Card> findCardsWithContentByDeck_Id(@Param("deckId") Long deckId);

    /**
     * Find card using id and include revisionSet
     *
     * @param cardId of the card
     * @return card including revisionSet
     */
    @EntityGraph(attributePaths = {"deck", "revisions", "latestRevision",
        "latestRevision.revisionEdit.imageFront", "latestRevision.revisionEdit.imageBack"})
    Optional<Card> findDetailsById(Long cardId);

    /**
     * Find card using id
     *
     * @param cardId of the card
     * @return card
     */
    @EntityGraph(attributePaths = {"deck", "latestRevision",
        "latestRevision.revisionEdit.imageFront", "latestRevision.revisionEdit.imageBack"})
    Optional<Card> findSimpleById(Long cardId);
}
