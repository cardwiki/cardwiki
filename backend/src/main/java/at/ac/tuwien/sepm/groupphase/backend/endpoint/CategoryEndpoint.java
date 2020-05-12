package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDetailedDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    @ApiOperation(value = "Get list of categories without details")
    public List<CategorySimpleDto> getCategories() {
        LOGGER.info("GET /api/v1/categories");
        return categoryMapper.categoryToCategorySimpleDto(categoryService.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new Category")
    public CategoryDetailedDto createCategory(@Valid @RequestBody CategoryInquiryDto categoryInquiryDto) {
        LOGGER.info("POST /api/v1/categories");
        try {
            return categoryMapper.categoryToCategoryDetailedDto(
                categoryService.createCategory(categoryMapper.categoryInquiryDtoToCategory(categoryInquiryDto)));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Parent.", e);
        }   
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get detailed information about a specific category")
    public CategoryDetailedDto getCategory(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/categories/{}", id);
        return categoryMapper.categoryToCategoryDetailedDto(categoryService.findOneById(id));
    }
}