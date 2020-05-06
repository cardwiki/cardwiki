package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository  extends JpaRepository<Category, Long> {

    /**
     * Find all category entries.
     *
     * @return list of all category entries
     */
    List<Category> findAll();

    /**
     * Find a category entry.
     *
     * @param id of the entry to find
     * @return category entry found
     */
    Category getOne(Long id);

    /**
     * Persist a category entry.
     *
     * @return Persisted category entry
     */
    Category save(Category category);
}
