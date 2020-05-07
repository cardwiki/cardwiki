package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        LOGGER.debug("Find categories.");
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category findOneById(Long id) {
        LOGGER.debug("Find category with id {}.", id);
        Category result = categoryRepository.findCategoryById(id);
        Hibernate.initialize(result.getChildren());
        return result;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        LOGGER.debug("Create category {}", category);
        Category parent = category.getParent();
        if (parent != null && !categoryRepository.existsById(parent.getId())) {
            throw new NotFoundException("Selected parent category not found.");
        }
        Category result = categoryRepository.save(category);
        result.setParent(findOneById(parent.getId()));
        return result;
    }
}
