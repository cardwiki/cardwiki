package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.CategoryNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.*;


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
        Category category = categoryRepository.findCategoryById(id).orElseThrow(
            () -> new CategoryNotFoundException("Category not found."));

        if (category.getParent() != null) {
            Category parent = categoryRepository.findCategoryById(category.getParent().getId()).orElseThrow(
                () -> new CategoryNotFoundException("Invalid parent reference."));
            category.setParent(parent);
        }
        if (category.getChildren() != null) {
            category.setChildren(new LinkedHashSet<>(categoryRepository.findChildren(id)));
        }

        return category;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        LOGGER.debug("Create category {}", category);
        User user = userService.loadCurrentUser();
        category.setCreatedBy(user);
        category.setName(category.getName().trim().replaceAll(" +", " "));

        return categoryRepository.saveAndFlush(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category category) {
        LOGGER.debug("Update category with id {}", id);
        if (!categoryRepository.existsById(id)) throw new CategoryNotFoundException("Category not found.");

        Category parent = category.getParent();
        if (parent != null) {
            category.setParent(parent);
            if (parent.getId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent.");
            }
            if (categoryRepository.ancestorExistsWithId(id, parent.getId())) {
                throw new IllegalArgumentException("Circular Child-Parent relation.");
            }
        }

        category.setId(id);
        category.setName(category.getName().trim().replaceAll(" +", " "));
        Category result = categoryRepository.saveAndFlush(category);
        Hibernate.initialize(result.getParent());
        return result;
    }
}
