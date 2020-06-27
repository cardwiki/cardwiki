package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.CategoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService  {

    /**
     * Find all category entries.
     *
     * @return list of all category entries sorted by name
     */
    List<Category> findAll();

    /**
     * Find categories containing {@code name} (case insensitive)
     *
     * @param name the search string
     * @param pageable the paging parameters
     * @return page of categories with names containing {@code name}
     */
    Page<Category> searchByName(String name, Pageable pageable);

    /**
     * Find a category entry.
     *
     * @param id of the entry to find
     * @return category entry found
     * @throws CategoryNotFoundException if no category with this id exists
     */
    Category findOneOrThrow(Long id);


    /**
     * Creates a category.
     *
     * @param category to be created
     * @return the category which has been created
     */
    Category createCategory(Category category);

    /**
     * Updates a category.
     *
     * @param id of the category to be updated.
     * @param category containing the data to update the category with
     * @return details of the category which has been updated
     */
    Category updateCategory(Long id, Category category);

    /**
     * Deletes a category.
     *
     * @param id of the category to delete.
     * @throws CategoryNotFoundException if no category with id {@code id} exists.
     */
    void deleteCategory(Long id);
}
