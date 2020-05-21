package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class DeckUpdateDto {

    @NotBlank
    private String name;

    private List<CategorySimpleDto> categories;
}
