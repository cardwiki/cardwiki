package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
    /**
     * Find all decks containing {@code name} (case insensitive)
     *
     * @param name the search string
     * @param pageable the paging parameters
     * @return ordered list of all decks with names containing {@code name}
     */
    @EntityGraph(attributePaths = {"createdBy", "categories"})
    Page<Deck> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find a specific deck by id
     * @param deckId of the deck to find
     * @return deck found
     */
    @EntityGraph(attributePaths = {"categories"})
    Optional<Deck> findById(Long deckId);

    /**
     * Find page of decks created by user
     *
     * @param id of the user to query
     * @param pageable pagination parameters for the query
     * @return page of decks created by user
     */
    @EntityGraph(attributePaths = {"createdBy"})
    Page<Deck> findByCreatedBy_Id(long id, Pageable pageable);

    /**
     * Find all decks created by user for exporting data
     *
     * @param userId of the user to query
     * @return all decks created by user
     */
    Set<Deck> findExportByCreatedBy_Id(long userId);

    /**
     * Find favorites of user
     *
     * @param userId id of the user
     * @param pageable pagination parameters for the query
     * @return page of favorites by user
     */
    Page<Deck> findByFavoredById(Long userId, Pageable pageable);

    /**
     * Find all favorites of user for exporting data
     *
     * @param userId id of the user
     * @return all favorites by user
     */
    Set<Deck> findExportByFavoredBy_Id(Long userId);

    /**
     * Check if a deck is a favorite of a user
     *
     * @param deckId id of the deck
     * @param userId id of the user
     * @return false if deck is not a favorite of the user, or if deck/user does not exist
     */
    boolean existsByIdAndFavoredById(Long deckId, Long userId);

    /**
     * Count how many cards a given deck contains that have the given status for the given user
     * @param deckId
     * @param userId
     * @param status
     * @return Count how many cards a given deck contains that have the given status for the given user
     */
    @Query("SELECT count(*) FROM Card c inner join RevisionEdit r on r=c.latestRevision LEFT JOIN Progress p ON c = p.id.card WHERE p.id.user.id = :userId AND c.deck.id = :deckId AND p.status = :status")
    int countProgressStatuses(@Param("deckId") Long deckId, @Param("userId") Long userId, @Param("status") Progress.Status status);

    /**
     * Count cards in deck
     * @param deckId
     * @return how many cards are in the deck
     */
    @Query("SELECT count(*) FROM Card c inner join RevisionEdit r on r=c.latestRevision WHERE c.deck.id = :deckId")
    int countCards(@Param("deckId") long deckId);
}
