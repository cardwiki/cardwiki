package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDetailedDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CategoryMappingTest extends TestDataGenerator {

    @Autowired
    private CategoryMapper categoryMapper;

   @Test
   @Transactional
    public void givenCategoryWithAllProperties_whenMapToCategoryDetailedDto_thenDtoHasAllProperties() {
        Category category = givenCategory();
        category.getChildren().add(new Category(3L, "Child category"));

       CategoryDetailedDto categoryDetailedDto = categoryMapper.categoryToCategoryDetailedDto(category);
        assertAll(
            () -> assertEquals(categoryDetailedDto.getId(), category.getId()),
            () -> assertEquals(categoryDetailedDto.getName(), category.getName()),
            () -> assertEquals(categoryDetailedDto.getParent().getName(), category.getParent().getName()),
            () -> assertEquals(categoryDetailedDto.getParent().getId(), category.getParent().getId()),
            () -> assertEquals(categoryDetailedDto.getCreatedBy(), category.getCreatedBy().getUsername()),
            () -> assertNotNull(categoryDetailedDto.getCreatedAt()),
            () -> assertNotNull(categoryDetailedDto.getUpdatedAt()),
            () -> assertEquals(categoryDetailedDto.getChildren().size(), 1),
            () -> assertEquals(categoryDetailedDto.getChildren().get(0).getName(), "Child category")
        );
    }

    @Test
    @Transactional
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

    @Test
    @Transactional
    public void givenValidCategoryInquiryDto_whenMapToCategory_thenGetCategoryEntityWithCorrespondingValues() {
        CategoryInquiryDto categoryInquiryDto = new CategoryInquiryDto();
        categoryInquiryDto.setName("test");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName("test parent");
        parent.setId(1L);
        categoryInquiryDto.setParent(parent);

        Category category = categoryMapper.categoryInquiryDtoToCategory(categoryInquiryDto);

        assertAll(
            () -> assertEquals(categoryInquiryDto.getName(), category.getName()),
            () -> assertEquals(categoryInquiryDto.getParent().getName(), category.getParent().getName()),
            () -> assertEquals(categoryInquiryDto.getParent().getId(), category.getParent().getId())
        );
    }
}
