package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDetailedDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Named("simpleCategory")
    CategorySimpleDto categoryToCategorySimpleDto(Category category);

    @IterableMapping(qualifiedByName = "simpleCategory")
    List<CategorySimpleDto> categoryToCategorySimpleDto(List<Category> categories);

    CategoryDetailedDto categoryToCategoryDetailedDto(Category category);

    Category categoryInquiryDtoToCategory(CategoryInquiryDto categoryInquiryDto);

}
