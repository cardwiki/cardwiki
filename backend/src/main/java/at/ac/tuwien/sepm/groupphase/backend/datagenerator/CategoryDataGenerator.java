package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class CategoryDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_CATEGORIES_TO_GENERATE = 3;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public CategoryDataGenerator(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    private void generateCategories() {
        User user = new User();
        user.setAuthId("fake id2");
        user.setDescription("test user2");
        user.setAdmin(false);
        user.setEnabled(false);
        user.setUsername("CoolFighter2006");
        userRepository.saveAndFlush(user);

        long countCards = categoryRepository.count();
        for (int i = (int) countCards; i < NUMBER_OF_CATEGORIES_TO_GENERATE; i++) {
            LOGGER.info("Creating category {}", i);
            Category category = new Category();
            category.setName("Test category " + i);
            categoryRepository.save(category);
        }
    }
}
