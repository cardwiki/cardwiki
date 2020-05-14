package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    @EntityGraph(attributePaths ={"decks", "createdBy"})
    Category findCategoryById(Long id);

    @EntityGraph(attributePaths = "decks")
    @Query("SELECT c FROM Category c WHERE c.parent.id=:parentId")
    List<Category> findChildren(@Param("parentId") Long parentId);
}
