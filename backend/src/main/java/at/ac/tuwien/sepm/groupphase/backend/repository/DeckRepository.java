package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long>, DeckRepositoryCustom {
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
}
