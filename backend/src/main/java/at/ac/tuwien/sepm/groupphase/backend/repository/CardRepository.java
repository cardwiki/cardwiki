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
import org.springframework.data.domain.Pageable;
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
    @EntityGraph(attributePaths = {"latestRevision"})
    List<Card> findCardsByDeck_Id(Long deckId);

    /**
     * Find cards of a specific deck, excludes currently empty ones
     *
     * @param deckId of the deck
     * @return list of cards of the deck, excluding currently empty ones
     */
    @EntityGraph(attributePaths = {"latestRevision"})
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
     * Find card using id
     *
     * @param cardId of the card
     * @return card
     */
    @EntityGraph(attributePaths = {"deck", "latestRevision"})
    Optional<Card> findById(@Param("cardId") Long cardId);

    @Query("select c from Card c left join Progress p on c.id = p.id.card.id and p.id.user.id = :userId where c.deck.id=:deckId and (current_timestamp >= p.due or p.due is null) order by p.status asc nulls first, p.due asc nulls first")
    List<Card> findNextCards(@Param("deckId") Long deckId, @Param("userId") Long userId, Pageable pageable);

    /**
     * Check if a card exists with certain content
     *
     * @param textFront card content parameter
     * @return true if such a card exists, false else
     */
    @Query("select case when count(r) > 0 then true else false end from Card c inner join RevisionEdit r on r.card.id=c.id" +
         " where r.textFront = :textFront and c.deck.id=:deckId")
    boolean existsByDeckAndRevisionEditContent(@Param("deckId") Long deckId, @Param("textFront") String textFront);
}
