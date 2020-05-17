package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    @Override
    public List<Category> findAll() {
        LOGGER.debug("Find categories.");
        return categoryRepository.findAll(Sort.by(Sort.Order.asc("name").ignoreCase()));
    }

    @Override
    @Transactional
    public Category findOneById(Long id) {
        LOGGER.debug("Find category with id {}.", id);
        Optional<Category> result = categoryRepository.findCategoryById(id);
        Category category;
        if (result.isPresent()) {
            category = result.get();
            if (category.getParent() != null) {
                category.setParent(categoryRepository.findCategoryById(category.getParent().getId()).get());
            }
            if (category.getChildren() != null) {
                category.setChildren(new HashSet<>(categoryRepository.findChildren(id)));
            }
        } else {
            throw new NotFoundException("Category not found.");
        }
        return category;
    }

    @Override
    @Transactional
    public Category createCategory(Category category, String oAuthId) {
        LOGGER.debug("Create category {}", category);
        User user = userService.loadUserByOauthId(oAuthId);
        category.setCreatedBy(user);
        Category parent = category.getParent();
        if (parent != null && !categoryRepository.existsById(parent.getId())) {
            throw new NotFoundException("Selected parent category does not exist in Database.");
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category category) {
        LOGGER.debug("Update category with id {}", id);
        if (!categoryRepository.existsById(id)) throw new NotFoundException("Category not found.");
        if (category.getParent() != null && category.getParent().getId().equals(id)) {
            throw new IllegalArgumentException("Category cannot be its own parent.");
        }
        if (category.getParent() != null && categoryRepository.parentExistsWithId(id, category.getParent().getId())) {
           throw new IllegalArgumentException("Circular Child-Parent relation.");
        }
        category.setId(id);
        return categoryRepository.save(category);
    }
}
