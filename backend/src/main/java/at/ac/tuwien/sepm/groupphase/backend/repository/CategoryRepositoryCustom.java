package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CategoryRepositoryCustom {

    boolean parentExistsWithId(Long id, Long parentId);

    void updateCategory(Long id, Category category);

}
