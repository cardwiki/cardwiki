package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class DeckInputDto {
    @NotBlank
    private String name;
}
