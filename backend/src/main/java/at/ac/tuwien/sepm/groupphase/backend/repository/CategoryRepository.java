package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * finds a category without loading parent or children
     *
     * @param id of the category to find
     * @return category found
     */
    @EntityGraph(attributePaths ={"decks", "createdBy"})
    Optional<Category> findCategoryById(Long id);

    /**
     * Find categories containing {@code name} (case insensitive)
     *
     * @param name the search string
     * @param pageable the paging parameters
     * @return page of categories with names containing {@code name}
     */
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * finds all children of a given parent
     *
     * @param parentId of the categories to find
     * @return children of category with id parentId
     */
    @Query("SELECT c FROM Category c WHERE c.parent.id=:parentId ORDER BY LOWER(c.name) ASC")
    List<Category> findChildren(@Param("parentId") Long parentId);

    /**
     * Find all categories created by user for exporting data
     *
     * @param userId id of the user
     * @return all categories by user
     */
    Set<Category> findExportByCreatedBy_Id(Long userId);

    /**
     * checks if there is a circular relation between two categories
     *
     * @param id of a category
     * @param parentId of the corresponding parent category
     * @return true if there is a circular relation between the two categories, else false
     */
    @Query(nativeQuery = true, value =
        "SELECT CASE WHEN EXISTS (" +
            "WITH link(id, parent_id, level) AS (" +
                "SELECT id, parent_id, 0 FROM categories WHERE categories.id=:parentId " +
                "UNION ALL " +
                "SELECT categories.id, categories.parent_id, LEVEL + 1 " +
                "FROM link INNER JOIN categories ON link.parent_id = categories.id " +
                "AND categories.id!=:parentId" +
                ") " +
            "SELECT id FROM link WHERE link.id=:id" +
        ") THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END"
    )
    boolean ancestorExistsWithId(@Param("id") Long id, @Param("parentId") Long parentId);
}
