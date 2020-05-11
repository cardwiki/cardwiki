package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTest extends TestDataGenerator {

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    public void givenNothing_whenSaveCategory_thenExistsByIdAndHasNameAndParent() {
        Category category = givenCategory();
        categoryRepository.saveAndFlush(category);
        assertAll(
            () -> assertTrue(categoryRepository.existsById(category.getId())),
            () -> assertNotNull(categoryRepository.getOne(category.getId()).getName()),
            () -> assertNotNull(categoryRepository.getOne(category.getId()).getParent())
        );
    }

    @Test
    public void givenCategoryWithParent_whenGetCategoryByIdWithParentId_thenIdIsNotNull() {
        Category category = givenCategory();
        categoryRepository.saveAndFlush(category);
        assertAll(
            () -> assertNotNull(category.getParent()),
            () -> assertNotNull(category.getParent().getId()),
            () -> assertNotNull(categoryRepository.findCategoryById(category.getParent().getId()))
        );
    }

    @Test
    public void givenCategoryWithoutName_whenSaveCategory_thenThrowsDataAccessException() {
        Category category = new Category();

        assertAll(
            () -> assertNull(category.getName()),
            () -> assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(category))
        );
    }
}
