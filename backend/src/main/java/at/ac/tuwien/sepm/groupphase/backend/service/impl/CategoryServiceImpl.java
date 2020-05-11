package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepositoryCustom;
import at.ac.tuwien.sepm.groupphase.backend.repository.impl.CategoryRepositoryImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;

import org.aspectj.weaver.ast.Not;
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
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category findOneById(Long id) {
        LOGGER.debug("Find category with id {}.", id);
        Category result = categoryRepository.getOne(id);
        if (result != null) {
            Hibernate.initialize(result.getChildren());
        } else {
            throw new NotFoundException("Category not found.");
        }
        return result;
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
        if (parent != null) {
            try {
                parent = findOneById(parent.getId());
            } catch(NotFoundException e) {
                throw e;
            }
        }
        Category result = categoryRepository.save(category);
        result.setParent(parent);
        return result;
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category category) {
        LOGGER.debug("Update category with id {}", id);
        if (!categoryRepository.existsById(id)) throw new NotFoundException("Category not found.");
        if (category.getParent() != null && category.getParent().getId() == id) {
            throw new IllegalArgumentException("Category cannot be its own parent.");
        }
        if (categoryRepository.childExistsWithId(id)) {
           throw new IllegalArgumentException("Circular Child-Parent relation.");
        }
        categoryRepository.updateCategory(id, category);

        try {
            return findOneById(id);
        } catch (NotFoundException e) {
            throw e;
        }
    }
}
