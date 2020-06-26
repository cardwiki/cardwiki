package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTest extends TestDataGenerator {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Test
    public void givenNothing_whenSaveCategoryWithoutCreatedBy_thenThrowDataIntegrityViolationException() {
        Category category = new Category();
        category.setName("foo");

        assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(category));
    }

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
    public void givenThreeCategories_whenFindAllSorted_thenReturnsListWithLengthThreeWhichContainsAllElementsInAlphabeticOrder() {
        Agent agent = persistentAgent();
        List<Category> categories = Arrays.asList(
            agent.createCategory("foo"),
            agent.createCategory("bar"),
            agent.createCategory("foobar")
        );
        categories.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER));

        assertAll(
            () -> assertEquals(categoryRepository.findAll(Sort.by(Sort.Order.asc("name").ignoreCase())).size(), 3),
            () -> assertEquals(categoryRepository.findAll(Sort.by(Sort.Order.asc("name").ignoreCase())), categories)
        );
    }

    @Test
    public void givenNothing_whenFindCategoryById_thenResultIsEmpty() {
        assertTrue(categoryRepository.findById(1L).isEmpty());
    }

    @Test
    public void givenCategory_whenCreateCategoryWithSameName_thenThrowsDataIntegrityViolationException() {
        Category category1 = givenCategory();
        Category category2 = new Category(2L, category1.getName());

        assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(category2));
    }

    @Test
    public void givenCategoryWithParent_whenAncestorExistsWithIdWithIdAsParentIdAndParentIdAsId_thenReturnsTrue() {
        Category category = givenCategory();

        assertTrue(categoryRepository.ancestorExistsWithId(category.getParent().getId(), category.getId()));
    }

    @Test
    public void givenCategoryWithParent_whenAncestorExistsWithIdWithIdAndIndependentCategoryId_thenReturnsFalse() {
        Agent agent = persistentAgent();
        Category category = agent.createCategory("foo");
        Category parent = agent.createCategory("bar");

        assertFalse(categoryRepository.ancestorExistsWithId(category.getId(), parent.getId()));
    }

    @Test
    public void givenCategoryWithParent_whenDeleteParent_thenDeleteChildCategoryAndNotDeck() {
        Deck deck = givenDeck();
        Category category = givenCategory();

        category.getDecks().add(deck);
        categoryRepository.save(category);

        categoryRepository.deleteById(category.getParent().getId());

        assertFalse(categoryRepository.existsById(category.getParent().getId()));
        assertFalse(categoryRepository.existsById(category.getId()));
        assertTrue(deckRepository.existsById(deck.getId()));
    }
}
