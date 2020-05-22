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
    public void givenNothing_whenSaveCategoryWithParent_thenExistsByIdAndHasParent() {
        Category category = givenCategory();

        assertAll(
            () -> assertTrue(categoryRepository.existsById(category.getId())),
            () -> assertNotNull(categoryRepository.getOne(category.getId()).getName()),
            () -> assertNotNull(categoryRepository.getOne(category.getId()).getParent()),
            () -> assertTrue(categoryRepository.existsById(category.getParent().getId()))
        );
    }

    @Test
    public void givenCategoryWithParent_whenFindCategoryById_thenReturnsCategoryWithParent() {
        Category category = givenCategory();

        assertAll(
            () -> assertEquals(categoryRepository.getOne(category.getId()), category),
            () -> assertEquals(categoryRepository.getOne(category.getParent().getId()), category.getParent())
        );
    }

    @Test
    public void givenCategoryWithoutName_whenSaveCategory_thenThrowsDataIntegrityViolationException() {
        Category category = new Category();

        assertAll(
            () -> assertNull(category.getName()),
            () -> assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(category))
        );
    }

    @Test
    public void givenTwoCategories_whenFindAll_thenReturnsListWithLengthTwoWhichContainsBothElements() {
        Category category1 = givenCategory();
        Category category2 = category1.getParent();

        assertAll(
            () -> assertEquals(categoryRepository.findAll().size(), 2),
            () -> assertTrue(categoryRepository.findAll().contains(category1)),
            () -> assertTrue(categoryRepository.findAll().contains(category2))
        );
    }

    @Test
    public void givenNothing_whenFindCategoryById_thenResultIsNull() {
        assertTrue(categoryRepository.findById(1L).isEmpty());
    }

    @Test
    public void givenCategory_whenCreateCategoryWithSameName_thenThrowsDataIntegrityViolationException() {
        Category category1 = givenCategory();
        Category category2 = new Category(2L, category1.getName());

        assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(category2));
    }
}