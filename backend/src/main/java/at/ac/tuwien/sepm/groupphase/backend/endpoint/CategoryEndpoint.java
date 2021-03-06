package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryDetailedDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.annotation.Secured;
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
    @ApiOperation(value = "Find categories by name")
    public Page<CategorySimpleDto> search(@RequestParam String name, @SortDefault("name") Pageable pageable) {
        LOGGER.info("GET /api/v1/categories?name={} {}", name, pageable);
        return categoryService.searchByName(name, pageable)
            .map(categoryMapper::categoryToCategorySimpleDto);
    }

    @PostMapping
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new Category", authorizations = {@Authorization("user")})
    public CategoryDetailedDto createCategory(@RequestBody @Valid CategoryInputDto categoryInputDto) {
        LOGGER.info("POST /api/v1/categories");
        return categoryMapper.categoryToCategoryDetailedDto(
            categoryService.createCategory(
                categoryMapper.categoryInputDtoToCategory(categoryInputDto)));
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get detailed information about a specific category")
    public CategoryDetailedDto getCategory(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/categories/{}", id);
        return categoryMapper.categoryToCategoryDetailedDto(categoryService.findOneOrThrow(id));
    }

    @Secured("ROLE_USER")
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Update category with specific id", authorizations = {@Authorization("user")})
    public CategoryDetailedDto updateCategory(@PathVariable Long id,
                                              @RequestBody @Valid CategoryInputDto categoryInputDto) {
        LOGGER.info("PUT /api/v1/categories/{}", id);
        return categoryMapper.categoryToCategoryDetailedDto(
            categoryService.updateCategory(
                id, categoryMapper.categoryInputDtoToCategory(categoryInputDto)));

    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Permanently delete a category", authorizations = {@Authorization("admin")})
    public void deleteCategory(@PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/categories/{}", id);
        categoryService.deleteCategory(id);
    }
}
