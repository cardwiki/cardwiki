package at.ac.tuwien.sepm.groupphase.backend.unittests;

    import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
    import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
    import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
    import at.ac.tuwien.sepm.groupphase.backend.entity.User;
    import at.ac.tuwien.sepm.groupphase.backend.exception.CategoryNotFoundException;
    import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
    import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
    import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
    import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
    import org.hibernate.exception.ConstraintViolationException;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.ArgumentCaptor;
    import org.mockito.Captor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.test.mock.mockito.MockBean;
    import org.springframework.dao.DataIntegrityViolationException;
    import org.springframework.data.domain.Sort;
    import org.springframework.test.context.ActiveProfiles;
    import org.springframework.test.context.junit.jupiter.SpringExtension;
    import java.sql.SQLException;
    import java.util.*;
    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.AdditionalAnswers.returnsFirstArg;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CategoryServiceTest extends TestDataGenerator {

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private UserService userService;


    @Test
    public void givenTwoCategories_whenFindAll_thenReturndOrderedListWithTwoElements() {
        Category category = getSampleCategoryWithParent();
        category.getDecks().add(getSampleDeck());
        List<Category> categories = new ArrayList<>();
        categories.add(category.getParent());
        categories.add(category);

        when(categoryRepository.findAll(any(Sort.class))).thenAnswer(i -> {
            if (i.getArgument(0).equals(Sort.by(Sort.Order.asc("name").ignoreCase()))) {
                categories.sort(Comparator.comparing(cat -> cat.getName(), String.CASE_INSENSITIVE_ORDER));
            }
            return categories;
        });
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAll();
        ArgumentCaptor<Sort> captor = ArgumentCaptor.forClass(Sort.class);
        verify(categoryRepository, atLeast(0)).findAll(captor.capture());
        if (captor.getAllValues().isEmpty()) {
            verify(categoryRepository, times(1)).findAll();
        } else {
            verify(categoryRepository, times(1)).findAll(any(Sort.class));
        }

        categories.sort(Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER));
        assertAll(
            () -> assertEquals(result.size(), 2),
            () -> assertEquals(result, categories)
        );
    }

    @Test
    public void givenCategoryWithParent_whenFindOneById_thenReturnsCategoryWithAllFields () {
        Category category = getSampleCategoryWithParent();
        Deck deck = getSampleDeck();
        category.getDecks().add(deck);
        Category child = new Category();
        child.setName("child category");
        child.setId(0L);
        child.setParent(category);
        category.getChildren().add(child);

        when(categoryRepository.findCategoryById(any(Long.class))).thenAnswer(i -> {
            if (i.getArgument(0).equals(category.getId())) {
                return Optional.of(category);
            } else if (i.getArgument(0).equals(category.getParent().getId())) {
                return Optional.of(category.getParent());
            }
            return Optional.of(category);
        });
        when(categoryRepository.findChildren(any(Long.class))).thenAnswer(i -> {
            List<Category> children = new ArrayList<>();
            if (i.getArgument(0).equals(category.getId())) {
                children.add(child);
            }
            return children;
        });

        Category result = categoryService.findOneById(category.getId());
        assertAll(
            () -> assertEquals(result, category),
            () -> assertEquals(result.getParent(), category.getParent()),
            () -> assertEquals(result.getCreatedBy(), category.getCreatedBy()),
            () -> assertEquals(result.getUpdatedAt(), category.getUpdatedAt()),
            () -> assertEquals(result.getChildren().size(), 1),
            () -> assertTrue(result.getChildren().contains(child)),
            () -> assertEquals(result.getDecks().size(), 1),
            () -> assertTrue(result.getDecks().contains(deck))
        );
    }

    @Test
    public void givenNothing_whenFindOneByIdNonExistentCategory_thenThrowsCategoryNotFoundException() {
        when(categoryRepository.findCategoryById(any(Long.class))).thenReturn(Optional.empty());

        Exception e = assertThrows(CategoryNotFoundException.class, () -> categoryService.findOneById(1L));
        assertEquals(e.getMessage(),"Category not found.");
    }

    @Test
    public void givenNothing_whenFindOneByIdNonExistentParent_thenThrowsCategoryNotFoundException() {
        Category category = getSampleCategoryWithParent();

        when(categoryRepository.findCategoryById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.findCategoryById(category.getParent().getId())).thenReturn(Optional.empty());

        Exception e = assertThrows(CategoryNotFoundException.class, () -> categoryService.findOneById(category.getId()));
        assertEquals(e.getMessage(),"Invalid parent reference.");
    }

    @Test
    public void givenCategoryWithoutParentOrChildren_whenFindOneById_thenReturnsCategoryWithoutParentOrChildren() {
        Category category = getSampleCategoryWithoutParent();

        when(categoryRepository.findCategoryById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.findChildren(category.getId())).thenReturn(Collections.emptyList());

        Category result = categoryService.findOneById(category.getId());
        assertAll(
            () -> assertEquals(category, result),
            () -> assertTrue(result.getParent() == null),
            () -> assertTrue(result.getChildren().isEmpty())
        );
    }

    @Test
    public void givenNothing_whenCreateCategory_thenReturnsCategoryWithAllFields() {
        Category category = getSampleCategoryWithParent();
        User user = getSampleUser();

        when(userService.loadCurrentUser()).thenReturn(user);
        when(categoryRepository.saveAndFlush(any(Category.class))).then(returnsFirstArg());
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        Category result = categoryService.createCategory(category);
        assertAll(
            () -> assertEquals(result, category),
            () -> assertEquals(result.getParent(), category.getParent()),
            () -> assertEquals(result.getCreatedBy(), user),
            () -> assertNotNull(result.getCreatedAt()),
            () -> assertNotNull(result.getUpdatedAt()),
            () -> assertTrue(result.getChildren().isEmpty()),
            () -> assertTrue(result.getDecks().isEmpty())
        );
    }

    @Test
    public void givenCategory_whenCreateCategoryWithSameName_thenThrowsIllegalArgumentException() {
        Category category = getSampleCategoryWithoutParent();

        DataIntegrityViolationException e = new DataIntegrityViolationException("", new ConstraintViolationException("", new SQLException(), "NAME_UNIQUE"));
        when(categoryRepository.saveAndFlush(category)).thenThrow(e);
        when(categoryRepository.save(category)).thenThrow(e);

        Exception ex = assertThrows(RuntimeException.class, () -> categoryService.createCategory(category));
        assertEquals(ex.getMessage(), "A category with that name already exists.");
    }

    @Test
    public void givenNothing_whenCreateCategoryWithNameContainingLeadingOrTrailingOrMultipleConsecutiveSpaces_thenReturnCategoryWithNameWithoutThoseSpaces() {
        Category category = new Category();
        category.setName("  category   x   ");

        when(categoryRepository.saveAndFlush(category)).thenReturn(category);

        assertEquals(categoryService.createCategory(category).getName(), "category x");
    }

    @Test
    public void givenCategory_whenUpdateCategoryWithValidData_thenReturnCategoryWithUpdatedFields() {
        Category category = getSampleCategoryWithoutParent();
        Category newParent = getUnconnectedSampleCategory2();
        Category update = new Category();
        update.setName("updated name");
        update.setParent(newParent);

        when(categoryRepository.findCategoryById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.findCategoryById(newParent.getId())).thenReturn(Optional.of(newParent));
        when(categoryRepository.ancestorExistsWithId(any(Long.class), any(Long.class))).thenReturn(false);
        when(categoryRepository.saveAndFlush(any(Category.class))).then(returnsFirstArg());
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        Category result = categoryService.updateCategory(category.getId(), update);

        assertAll(
            () -> assertEquals(result.getName(), update.getName()),
            () -> assertEquals(result.getId(), category.getId()),
            () -> assertEquals(result.getParent(), newParent),
            () -> assertNotEquals(result.getUpdatedAt(), result.getCreatedAt()),
            () -> assertTrue(result.getChildren().isEmpty()),
            () -> assertTrue(result.getDecks().isEmpty())
        );
    }

    @Test
    public void givenNothing_whenUpdateNonExistentCategory_thenThrowsCategoryNotFoundException() {
        when(categoryRepository.findCategoryById(any(Long.class))).thenReturn(Optional.empty());

        Exception e = assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(1L, null));
        assertEquals(e.getMessage(),"Category not found.");
    }

    @Test
    public void givenCategoryWithParent_whenUpdateWithSelfAsParent_thenThrowsIllegalArgumentException() {
        Category category = getSampleCategoryWithoutParent();
        Category update = new Category();
        update.setName("self parent test");
        update.setParent(category);

        when(categoryRepository.findCategoryById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.ancestorExistsWithId(any(Long.class), any(Long.class))).thenReturn(true);
        when(categoryRepository.saveAndFlush(any(Category.class))).then(returnsFirstArg());
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        Exception e = assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(category.getId(), update));
        assertEquals(e.getMessage(), "Category cannot be its own parent.");
    }

    @Test
    public void givenCategoryWithParent_whenUpdateWithChildAsParent_thenThrowsIllegalArgumentException() {
        Category category = getSampleCategoryWithParent();
        Category update = new Category();
        update.setName("circular relation test");
        update.setParent(category);

        when(categoryRepository.findCategoryById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.findCategoryById(category.getParent().getId())).thenReturn(Optional.of(category.getParent()));
        when(categoryRepository.ancestorExistsWithId(any(Long.class), any(Long.class))).thenReturn(true);
        when(categoryRepository.saveAndFlush(any(Category.class))).then(returnsFirstArg());
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        Exception e = assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(category.getParent().getId(), update));
        assertEquals(e.getMessage(), "Circular child-parent relation.");
    }

    @Test
    public void givenTwoCategories_whenUpdateCategoryWithExistingName_thenThrowsIllegalArgumentException() {
        Category category = getSampleCategoryWithoutParent();
        DataIntegrityViolationException e = new DataIntegrityViolationException("", new ConstraintViolationException("", new SQLException(), "NAME_UNIQUE"));

        when(categoryRepository.saveAndFlush(any(Category.class))).thenThrow(e);
        when(categoryRepository.save(any(Category.class))).thenThrow(e);
        when(categoryRepository.findCategoryById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.ancestorExistsWithId(any(Long.class), any(Long.class))).thenReturn(false);
        Exception ex = assertThrows(RuntimeException.class, () -> categoryService.updateCategory(
            category.getId(), new Category(category.getName(), null))
        );
        assertEquals(ex.getMessage(), "A category with that name already exists.");
    }

    @Test
    public void givenNothing_whenUpdateCategoryWithNameContainingLeadingOrTrailingOrMultipleConsecutiveSpaces_thenReturnCategoryWithNameWithoutThoseSpaces() {
        Category category = getSampleCategoryWithoutParent();
        Category update = new Category();
        update.setName("  category   x   ");

        when(categoryRepository.findCategoryById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.ancestorExistsWithId(any(Long.class), any(Long.class))).thenReturn(true);
        when(categoryRepository.saveAndFlush(any(Category.class))).then(returnsFirstArg());
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        assertEquals(categoryService.updateCategory(category.getId(), update).getName(), "category x");
    }
}
