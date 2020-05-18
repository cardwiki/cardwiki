package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategorySimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.USER_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.mockLogin;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CategoryEndpointTest extends TestDataGenerator {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    public void createCategoryReturnsCategoryDetails() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        CategoryInquiryDto categoryInquiryDto = new CategoryInquiryDto();
        categoryInquiryDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(category.getName());
        parent.setId(category.getId());
        categoryInquiryDto.setParent(parent);

        mvc.perform(post("/api/v1/categories")
            .with(mockLogin(USER_ROLES, user.getOAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInquiryDto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.name").value("blubb"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.parent.id").value(category.getId()))
            .andExpect(jsonPath("$.parent.name").value(category.getName()))
            .andExpect(jsonPath("$.createdAt").value(IsNull.notNullValue()));
    }

    @Test
    @Transactional
    public void updateCategoryWithValidDataReturnsUpdatedCategoryDetails() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        Category newParent = getCategoryRepository().save(new Category("bla", null));
        CategoryInquiryDto categoryInquiryDto = new CategoryInquiryDto();
        categoryInquiryDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(newParent.getName());
        parent.setId(newParent.getId());
        categoryInquiryDto.setParent(parent);

        mvc.perform(put("/api/v1/categories/{id}", category.getId())
            .with(mockLogin(USER_ROLES, user.getOAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInquiryDto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.name").value("blubb"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.parent.id").value(newParent.getId()))
            .andExpect(jsonPath("$.parent.name").value(newParent.getName()));
    }

    @Test
    @Transactional
    public void getCategoriesReturnsFullListOfCategories() throws Exception {
        getCategoryRepository().saveAndFlush(new Category("test2", null));
        getCategoryRepository().saveAndFlush(new Category("test1", null));

        mvc.perform(get("/api/v1/categories"))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name").value("test1"))
            .andExpect(jsonPath("$[1].name").value("test2"));
    }

    @Test
    @Transactional
    public void getCategoryReturnsCategoryDetails() throws Exception {
        Category category = givenCategory();
        String user = category.getCreatedBy().getUsername();


        ;
        mvc.perform(get("/api/v1/categories/{id}", category.getId()))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.name").value(category.getName()))
            .andExpect(jsonPath("$.id").value(category.getId()))
            .andExpect(jsonPath("$.parent.name").value(category.getParent().getName()))
            .andExpect(jsonPath("$.createdBy").value(user));

    }

    @Test
    @Transactional
    public void updateCategoryWithParentWhichIsAlsoChildThrowsBadRequest() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        CategoryInquiryDto categoryInquiryDto = new CategoryInquiryDto();
        categoryInquiryDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(category.getName());
        parent.setId(category.getId());
        categoryInquiryDto.setParent(parent);

        mvc.perform(put("/api/v1/categories/{categoryId}", category.getParent().getId())
            .with(mockLogin(USER_ROLES, user.getOAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInquiryDto)))
            .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void updateCategoryWithSelfAsParentThrowsBadRequest() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        CategoryInquiryDto categoryInquiryDto = new CategoryInquiryDto();
        categoryInquiryDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(category.getName());
        parent.setId(category.getId());
        categoryInquiryDto.setParent(parent);

        mvc.perform(put("/api/v1/categories/{categoryId}", category.getId())
            .with(mockLogin(USER_ROLES, user.getOAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInquiryDto)))
            .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void createCategoryWithNonExistentParentThrowsNotFound() throws Exception {
        givenCategory();
        User user = givenApplicationUser();
        CategoryInquiryDto categoryInquiryDto = new CategoryInquiryDto();
        categoryInquiryDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName("blubb parent");
        parent.setId(0L);
        categoryInquiryDto.setParent(parent);

        mvc.perform(post("/api/v1/categories")
            .with(mockLogin(USER_ROLES, user.getOAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInquiryDto)))
            .andExpect(status().is(404));
    }

    @Test
    public void getCategoryWhichDoesNotExistThrowsNotFound() throws Exception {
        mvc.perform(get("/api/v1/categories/{id}", 0L))
            .andExpect(status().is(404));
    }

    @Test
    @Transactional
    public void updateCategoryWhichDoesNotExistThrowsNotFound() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        CategoryInquiryDto categoryInquiryDto = new CategoryInquiryDto();
        categoryInquiryDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(category.getParent().getName());
        parent.setId(category.getParent().getId());
        categoryInquiryDto.setParent(parent);

        mvc.perform(put("/api/v1/categories/{id}", 0L)
            .with(mockLogin(USER_ROLES, user.getOAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInquiryDto)))
            .andExpect(status().is(404));
    }
}
