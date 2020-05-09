package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckRepository extends JpaRepository<Deck, Long> {
    /**
     * Find all decks containing {@code name} (case insensitive)
     *
     * @param name the search string
     * @param pageable the paging parameters
     * @return ordered list of all decks with names containing {@code name}
     */
    List<Deck> findByNameContainingIgnoreCase(String name, Pageable pageable);
}