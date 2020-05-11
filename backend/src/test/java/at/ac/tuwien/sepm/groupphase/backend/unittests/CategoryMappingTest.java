package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDetailedDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CategoryMappingTest extends TestDataGenerator {

    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    public void givenCategoryWithAllProperties_whenMapToCategoryDetailedDto_thenDtoHasAllProperties() {
        User user = new User();
        user.setOAuthId("testid");
        user.setUsername("Test User");
        Category parent = new Category();
        parent.setName("Test Parent");
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setParent(parent);
        category.setCreatedAt(new Date());
        category.setUpdatedAt(new Date());
        category.setCreatedBy(user);

        CategoryDetailedDto categoryDetailedDto = categoryMapper.categoryToCategoryDetailedDto(category);
        assertAll(
            () -> assertEquals(categoryDetailedDto.getId(), 1L),
            () -> assertEquals(categoryDetailedDto.getName(), "Test Category"),
            () -> assertEquals(categoryDetailedDto.getParent().getName(), "Test Parent"),
            () -> assertEquals(categoryDetailedDto.getCreatedBy().getUsername(), "Test User"),
            () -> assertNotNull(categoryDetailedDto.getCreatedAt()),
            () -> assertNotNull(categoryDetailedDto.getUpdatedAt())
        );
    }

    @Test
    public void givenListOfTwoCategories_whenMapToCategorySimpleDto_thenGetListWithSizeTwoWithCorrespondingValues() {
        List<Category> categories = new ArrayList<>();
        Category category1 = givenCategory();
        Category category2 = new Category();
        category2.setName("Second User");
        categories.add(category1);
        categories.add(category2);

        List<CategorySimpleDto> categoryDtos = categoryMapper.categoryToCategorySimpleDto(categories);

        assertAll(
            () -> assertEquals(2, categories.size()),
            () -> assertEquals("Test Category", categoryDtos.get(0).getName()),
            () -> assertEquals("Second User", categoryDtos.get(1).getName())
        );
    }
}
