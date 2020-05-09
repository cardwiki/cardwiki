package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Named("simpleCategory")
    SimpleCategoryDto categoryToSimpleCategoryDto(Category category);

    @IterableMapping(qualifiedByName = "simpleCategory")
    List<SimpleCategoryDto> categoryToSimpleCategoryDto(List<Category> categories);

    DetailedCategoryDto categoryToDetailedCategoryDto(Category category);

    Category categoryInquiryDtoToCategory(CategoryInquiryDto categoryInquiryDto);

}
