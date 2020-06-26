package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    /**
     * finds a category without loading parent or children
     *
     * @param id of the category to find
     * @return category found
     */
    @EntityGraph(attributePaths ={"decks", "createdBy"})
    Optional<Category> findCategoryById(Long id);

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
}
