package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    /**
     * Find cards for a specific deck
     *
     * @param deckId of the deck
     * @return list of cards of the deck
     */
    List<Card> findCardsByDeck_Id(Long deckId);

    /**
     * Find cards of a specific deck, excludes currently empty ones
     *
     * @param deckId of the deck
     * @return list of cards of the deck, excluding currently empty ones
     */
    @Query(value="select c from Card c inner join RevisionEdit r on r=c.latestRevision where c.deck.id=:deckId")
    Stream<Card> findCardsWithContentByDeck_Id(@Param("deckId") Long deckId);

    /**
     * Find latest revisions of a specific deck, excluding deleted ones
     *
     * @param deckId of the deck
     * @return latest revisions of the deck, excluding deleted ones
     */
    @Query(value="select r from Card c inner join RevisionEdit r on r=c.latestRevision where c.deck.id=:deckId")
    Stream<RevisionEdit> findLatestEditRevisionsByDeck_Id(@Param("deckId") Long deckId);

    /**
     * Find latest revisions of a specific deck, excluding deleted ones
     *
     * @param deckId of the deck
     * @return latest revisions of the deck, excluding deleted ones
     */
    @Query(value="select r from Card c inner join RevisionEdit r on r=c.latestRevision where c.deck.id=:deckId")
    Page<RevisionEdit> findLatestEditRevisionsByDeck_Id(@Param("deckId") Long deckId, Pageable pageable);

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
