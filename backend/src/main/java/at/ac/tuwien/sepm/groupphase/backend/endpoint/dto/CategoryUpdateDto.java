package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.*;
import java.util.Objects;

public class CategoryUpdateDto extends CategoryInquiryDto {

    @NotNull
    @Min(1)
    private Long id;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryUpdateDto)) return false;
        if (!super.equals(o)) return false;
        CategoryUpdateDto that = (CategoryUpdateDto) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }

    @Override
    public String toString() {
        return "CategoryUpdateDto{" +
            "id=" + id +
            '}';
    }

    public static final class CategoryUpdateDtoBuilder {
        private Long id;
        private String name;
        private Long parentId;

        private CategoryUpdateDtoBuilder() {
        }

        public static CategoryUpdateDto.CategoryUpdateDtoBuilder aCategoryUpdateDto() {
            return new CategoryUpdateDto.CategoryUpdateDtoBuilder();
        }

        public CategoryUpdateDto.CategoryUpdateDtoBuilder withId(Long Id) {
            this.id = id;
            return this;
        }

        public CategoryUpdateDto.CategoryUpdateDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CategoryUpdateDto.CategoryUpdateDtoBuilder withParentId(Long parentId) {
            this.parentId = parentId;
            return this;
        }

        public CategoryUpdateDto build() {
            CategoryUpdateDto categoryUpdateDto = new CategoryUpdateDto();
            categoryUpdateDto.setId(id);
            categoryUpdateDto.setName(name);
            categoryUpdateDto.setParentId(parentId);
            return categoryUpdateDto;
        }
    }
}
