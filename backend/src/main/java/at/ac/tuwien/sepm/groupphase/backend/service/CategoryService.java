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
     * Creates a category.
     *
     * @param category to be created
     * @return the category which has been created
     */
    Category createCategory(Category category);

}
