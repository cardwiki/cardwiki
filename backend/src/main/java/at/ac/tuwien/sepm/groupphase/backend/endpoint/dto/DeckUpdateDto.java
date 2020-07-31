package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.validation.NullOrNotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class DeckUpdateDto {

    @NullOrNotBlank
    private String name;

    private Set<@NotNull Long> categories;
}
