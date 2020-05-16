package at.ac.tuwien.sepm.groupphase.backend.repository;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CategoryRepositoryCustom {

    /**
     * checks if there is a circular relation between to categories
     *
     * @param id of a category
     * @param parentId of the corresponding parent category
     * @return true if there is a circular relation between the two categories
     */
    boolean parentExistsWithId(Long id, Long parentId);

}
