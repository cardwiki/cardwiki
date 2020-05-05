package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        LOGGER.debug("Find categories.");
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        LOGGER.debug("Create category {}", category);
        return categoryRepository.save(category);
    }
}
