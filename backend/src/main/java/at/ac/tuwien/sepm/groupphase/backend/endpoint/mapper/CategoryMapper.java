package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDetailedDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Named("simpleCategory")
    CategorySimpleDto categoryToCategorySimpleDto(Category category);

    @IterableMapping(qualifiedByName = "simpleCategory")
     List<CategorySimpleDto> categoryToCategorySimpleDto(List<Category> categories);

    @Mapping(source = "category.createdBy.username", target = "createdBy")
    @Mapping(source = "children", target = "children", qualifiedByName = "childrenToSimpleChildren")
    @Mapping(source = "parent", target = "parent", qualifiedByName="parentToSimpleParent")
    CategoryDetailedDto categoryToCategoryDetailedDto(Category category);

    @Named("childrenToSimpleChildren")
    static List<CategorySimpleDto> childrenToSimpleChildren(Set<Category> children) {
        return INSTANCE.categoryToCategorySimpleDto(new ArrayList<>(children));
    }

    @Named("parentToSimpleParent")
    static CategorySimpleDto parentToSimpleParent(Category parent) {
        return INSTANCE.categoryToCategorySimpleDto(parent);
    }

     Category categoryInquiryDtoToCategory(CategoryInquiryDto categoryInquiryDto);

}
