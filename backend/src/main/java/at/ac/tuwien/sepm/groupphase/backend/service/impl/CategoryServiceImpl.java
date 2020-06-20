package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.CategoryNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;

import org.hibernate.Hibernate;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
    public Category findOneOrThrow(Long id) {
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
        User user = userService.loadCurrentUserOrThrow();
        category.setCreatedBy(user);
        category.setName(category.getName().trim().replaceAll(" +", " "));
        try {
            return categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(handleDataIntegrityViolationException(e));
        }
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category categoryUpdate) {
        LOGGER.debug("Update category with id {}", id);
        Category category = findOneOrThrow(id);

        Category parent = null;
        Category newParent = categoryUpdate.getParent();
        if (newParent != null) {
            if (newParent.getId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent.");
            }
            if (categoryRepository.ancestorExistsWithId(id, newParent.getId())) {
                throw new IllegalArgumentException("Circular child-parent relation.");
            }
            parent = findOneOrThrow(newParent.getId());
        }

        category.setParent(parent);
        category.setName(categoryUpdate.getName().trim().replaceAll(" +", " "));
        try {
            category = categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(handleDataIntegrityViolationException(e));
        }
        Hibernate.initialize(category.getParent());
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        LOGGER.debug("Delete category with id {}", id);
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }

    private String handleDataIntegrityViolationException (DataIntegrityViolationException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            String cause = ((ConstraintViolationException) e.getCause()).getConstraintName().toLowerCase();
            if (cause.contains("name_unique")) {
                return "A category with that name already exists.";
            } else {
                return cause;
            }
        }
        return e.getMessage();
    }
}
