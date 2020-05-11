package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;

public interface CategoryRepositoryCustom {

    boolean childExistsWithId(Long id);

    void updateCategory(Long id, Category category);
}
