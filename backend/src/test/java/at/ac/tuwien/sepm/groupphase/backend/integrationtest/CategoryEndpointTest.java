package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CategoryInputDto;
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

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.security.MockedLogins.*;
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
    public void createCategoryReturnsCategoryDetails() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(category.getName());
        parent.setId(category.getId());
        categoryInputDto.setParent(parent);

        mvc.perform(post("/api/v1/categories")
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.name").value("blubb"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.parent.id").value(category.getId()))
            .andExpect(jsonPath("$.parent.name").value(category.getName()))
            .andExpect(jsonPath("$.createdAt").value(IsNull.notNullValue()));
    }

    @Test
    public void createCategoryWithNonExistentParentThrowsBadRequest() throws Exception {
        User user = givenApplicationUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName("blubb parent");
        parent.setId(0L);
        categoryInputDto.setParent(parent);

        mvc.perform(post("/api/v1/categories")
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(400));
    }

    @Test
    public void createCategoryWithParentWithoutIdThrowsBadRequest() throws Exception {
        Category category = givenCategory();
        User user = getSampleUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(category.getName());
        parent.setId(null);
        categoryInputDto.setParent(parent);

        mvc.perform(post("/api/v1/categories")
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void createCategoryWithDuplicateNameThrowsBadRequest() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName(category.getName());
        categoryInputDto.setParent(null);

        mvc.perform(post("/api/v1/categories")
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void createCategoryWithBlankNameStringThrowsBadRequest() throws Exception {
        User user = getSampleUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("   ");
        categoryInputDto.setParent(null);

        mvc.perform(post("/api/v1/categories")
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(400));
    }

    @Test
    public void getCategoryReturnsCategoryDetails() throws Exception {
        Category category = givenCategory();

        mvc.perform(get("/api/v1/categories/{id}", category.getId()))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.name").value(category.getName()))
            .andExpect(jsonPath("$.id").value(category.getId()))
            .andExpect(jsonPath("$.parent.name").value(category.getParent().getName()))
            .andExpect(jsonPath("$.createdBy").value(category.getCreatedBy().getId()));

    }

    @Test
    public void getCategoryWhichDoesNotExistThrowsNotFound() throws Exception {
        mvc.perform(get("/api/v1/categories/{id}", 0L))
            .andExpect(status().is(404));
    }

    @Test
    @Transactional
    public void updateCategoryWithValidDataReturnsUpdatedCategoryDetails() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        Category parent = getCategoryRepository().save(new Category("valid name", null));
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("blubb");
        CategorySimpleDto parentDto = new CategorySimpleDto();
        parentDto.setName(parent.getName());
        parentDto.setId(parent.getId());
        categoryInputDto.setParent(parentDto);

        mvc.perform(put("/api/v1/categories/{id}", category.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(200))
            .andExpect(jsonPath("$.name").value("blubb"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.parent.id").value(parent.getId()))
            .andExpect(jsonPath("$.parent.name").value(parent.getName()));
    }

    @Test
    public void updateCategoryWithParentWhichIsAlsoChildThrowsBadRequest() throws Exception {
        Category category = givenCategory();
        User user = givenApplicationUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(category.getName());
        parent.setId(category.getId());
        categoryInputDto.setParent(parent);

        mvc.perform(put("/api/v1/categories/{categoryId}", category.getParent().getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(400));
    }

    @Test
    public void updateCategoryWithSelfAsParentThrowsBadRequest() throws Exception {
        Category category = givenCategory();
        User user = getUnconnectedSampleUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("blubb");
        CategorySimpleDto parent = new CategorySimpleDto();
        parent.setName(category.getName());
        parent.setId(category.getId());
        categoryInputDto.setParent(parent);

        mvc.perform(put("/api/v1/categories/{categoryId}", category.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(400));
    }

    @Test
    public void updateCategoryWhichDoesNotExistThrowsNotFound() throws Exception {
        User user = getSampleUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("blubb");
        categoryInputDto.setParent(null);

        mvc.perform(put("/api/v1/categories/{id}", 0L)
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(404));
    }

    @Test
    public void updateCategoryWithBlankNameStringThrowsBadRequest() throws Exception {
        Category category = getSampleCategoryWithoutParent();
        User user = givenApplicationUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName("    ");

        mvc.perform(put("/api/v1/categories/{id}", category.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(400));
    }

    @Test
    public void updateCategoryWithDuplicateNameThrowsBadRequest() throws Exception {
        Category category = givenCategory();
        User user = getSampleUser();
        CategoryInputDto categoryInputDto = new CategoryInputDto();
        categoryInputDto.setName(category.getParent().getName());

        mvc.perform(put("/api/v1/categories/{id}", category.getId())
            .with(mockLogin(USER_ROLES, user.getAuthId()))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(categoryInputDto)))
            .andExpect(status().is(400));
    }

    @Test
    public void userDeletesCategoryThrowsForbidden() throws Exception {
        User user = givenApplicationUser();
        Category category = givenCategory();

        mvc.perform(delete("/api/v1/categories/{id}", category.getId())
            .with(login(user.getAuthId())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void adminDeletesNonExistentCategoryReturnOk() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);

        mvc.perform(delete("/api/v1/categories/{id}", 404)
            .with(login(admin.getAuthId())))
            .andExpect(status().isOk());
    }

    @Test
    public void adminDeletesCategory() throws Exception {
        User admin = givenApplicationUser();
        admin.setAdmin(true);
        Category category = givenCategory();

        mvc.perform(delete("/api/v1/categories/{id}", category.getId())
            .with(login(admin.getAuthId())))
            .andExpect(status().isOk());
    }
}
