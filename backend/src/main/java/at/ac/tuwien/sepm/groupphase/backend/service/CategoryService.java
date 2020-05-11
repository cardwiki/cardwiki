package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;

import java.util.List;

public interface CategoryService  {

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
    Category findOneById(Long id);


    /**
     * Creates a category.
     *
     * @param category to be created
     * @param oAuthId of the user who triggered the request
     * @return the category which has been created
     */
    Category createCategory(Category category, String oAuthId);
}
