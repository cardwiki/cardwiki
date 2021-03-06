package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.validation.ParentIdValid;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.*;

@Data
public class CategoryInputDto {

    @NotBlank(message = "Name must not be null nor empty.")
    @Length(max = 200, message = "Name exceeds size limit.")
    private String name;

    @ParentIdValid
    private CategorySimpleDto parent;

}
