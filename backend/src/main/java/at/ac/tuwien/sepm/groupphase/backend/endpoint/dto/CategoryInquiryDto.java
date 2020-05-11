package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.validation.ParentIdValid;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.*;
import java.util.Objects;

public class CategoryInquiryDto {

    @NotEmpty(message = "Name must not be null nor empty.")
    @Length(max = 200, message = "Name exceeds size limit.")
    private String name;

    @ParentIdValid
    private Category parent;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Category getParent() { return parent; }

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

}
