package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.UniqueConstraint;
import javax.validation.constraints.*;
import java.util.Objects;

@Data
public class CategoryInquiryDto {

    @NotNull(message = "Name must not be null.")
    @Length(max = 200, message = "Name exceeds size limit.")
    private String name;

    private Category parent;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Category getParent() { return parent;  }

    public void setParent(Category parent) { this.parent = parent; }
}
