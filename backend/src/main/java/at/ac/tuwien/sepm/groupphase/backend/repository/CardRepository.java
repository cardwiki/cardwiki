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
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
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


    // Using native query due to performance issues with the jqpl version (> 500ms for 1000 cards with progress)
    static final String findNextCardsQuery = " FROM cards c " +
        " INNER JOIN revision_edits re ON c.latest_revision = re.id" +
        " LEFT OUTER JOIN progress p ON (" +
            " c.id = p.card_id " +
            " AND p.user_id = :userId" +
            " AND p.reverse = :reverse" +
        " )" +
        " WHERE c.deck_id = :deckId AND (p.due IS NULL OR NOW() >= p.due)";
    static final String findNextCardsOrder = " ORDER BY p.status ASC NULLS FIRST, p.due ASC NULLS FIRST";
    /**
     * Find next cards to learn
     *
     * @param deckId of the deck
     * @param userId of the learning user
     * @param reverse learn back to front instead of front to back
     * @param pageable pagination config
     */
    @Query(nativeQuery = true,
        value = "SELECT *" + findNextCardsQuery + findNextCardsOrder,
        countQuery = "SELECT count(*)" + findNextCardsQuery)
    Page<Card> findNextCards(@Param("deckId") Long deckId, @Param("userId") Long userId, @Param("reverse") boolean reverse, Pageable pageable);

    /**
     * Number of currently due cards.
     * Same as totalElements of {@link #findNextCards(Long, Long, boolean, Pageable)}
     */
    @Query(nativeQuery = true,
        value = "SELECT count(*)" + findNextCardsQuery)
    long dueCardsCount(@Param("deckId") Long deckId, @Param("userId") Long userId, @Param("reverse") boolean reverse);

    /**
     * Filter front texts by existing front texts in deck
     *
     * @param deckId id of the deck
     * @param frontTexts collection of front texts that should be filtered
     * @return all elements from frontTexts that exist in deck
     */
    @Query("select r.textFront from RevisionEdit r" +
        " inner join Card c on r = c.latestRevision" +
        " where c.deck.id = :deckId" +
        "   and r.textFront in :textFronts")
    Set<String> filterExistingFrontTexts(@Param("deckId") Long deckId, @Param("textFronts") Collection<String> frontTexts);
}
