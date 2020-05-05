package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleCategoryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CategoryMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/categories")
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
    @ApiOperation(value = "Get list of categories without details", authorizations = {@Authorization(value = "apiKey")})
    public List<SimpleCategoryDto> findAll() {
        LOGGER.info("GET /api/v1/categories");
        return categoryMapper.categoryToSimpleCategoryDto(categoryService.findAll());
    }
}
