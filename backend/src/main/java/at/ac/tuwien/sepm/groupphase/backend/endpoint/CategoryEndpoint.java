package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;


@RestController
@RequestMapping(value = "api/v1/categories")
public class CategoryEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryEndpoint(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
   // @ApiOperation(value = "Get list of categories without details", authorizations = {@Authorization(value = "apiKey")})
    public List<SimpleCategoryDto> getCategories() {
        LOGGER.info("GET /api/v1/categories");
        return categoryMapper.categoryToSimpleCategoryDto(categoryService.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetailedCategoryDto createCategory(@Valid @RequestBody CategoryInquiryDto categoryDto) {
        LOGGER.info("POST /api/v1/categories");
       return categoryMapper.categoryToDetailedCategoryDto(
           categoryService.createCategory(categoryMapper.categoryInquiryDtoToCategory(categoryDto)));
    }

    @GetMapping(value = "/{id}")
    public DetailedCategoryDto getCategory(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/categories/{}", id);
        return categoryMapper.categoryToDetailedCategoryDto(categoryService.findOneById(id));
    }
}
