package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

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
    List<Deck> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find a specific deck by id
     * @param deckId of the deck to find
     * @return deck found
     */
    @EntityGraph(attributePaths = {"categories"})
    Optional<Deck> findById(Long deckId);

    /**
     * Find all decks created by user
     *
     * @param id of the user to query
     * @param pageable pagination parameters for the query
     * @return ordered list of all decks created by user
     */
    @EntityGraph(attributePaths = {"createdBy"})
    List<Deck> findByCreatedBy_Id(long id, Pageable pageable);

    /**
     * Find favorites of user
     *
     * @param userId id of the user
     * @param pageable pagination parameters for the query
     * @return page of favorites by user
     */
    Page<Deck> findByFavoredById(Long userId, Pageable pageable);

    /**
     * Check if a deck is a favorite of a user
     *
     * @param deckId id of the deck
     * @param userId id of the user
     * @return false if deck is not a favorite of the user, or if deck/user does not exist
     */
    boolean existsByIdAndFavoredById(Long deckId, Long userId);
}
