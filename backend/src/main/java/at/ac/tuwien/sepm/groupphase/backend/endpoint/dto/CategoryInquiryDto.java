package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Objects;

public class CategoryInquiryDto {

    @NotNull(message = "Name must not be null.")
    @Length(max = 200, message = "Name exceeds size limit.")
    @Pattern(regexp="^[a-zA-Z0-9]+[a-zA-Z0-9 \\/\\-\\.\\,]+$",
        message="Invalid String: First character not alphanumeric or contains forbidden characters.")
    private String name;

    private Category parent;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Category getParent() { return parent;  }

    public void setParent(Category parent) { this.parent = parent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryInquiryDto)) return false;
        CategoryInquiryDto that = (CategoryInquiryDto) o;
        return getName().equals(that.getName()) &&
            Objects.equals(getParent(), that.getParent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getParent());
    }

    public static final class CategoryInquiryDtoBuilder {
        private String name;
        private Category parent;

        private CategoryInquiryDtoBuilder() {
        }

        public static CategoryInquiryDto.CategoryInquiryDtoBuilder aCategoryInquiryDto() {
            return new CategoryInquiryDto.CategoryInquiryDtoBuilder();
        }

        public CategoryInquiryDto.CategoryInquiryDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CategoryInquiryDto.CategoryInquiryDtoBuilder withParent(Category parent) {
            this.parent = parent;
            return this;
        }

        public CategoryInquiryDto build() {
            CategoryInquiryDto categoryInquiryDto = new CategoryInquiryDto();
            categoryInquiryDto.setName(name);
            categoryInquiryDto.setParent(parent);
            return categoryInquiryDto;
        }
    }
}
