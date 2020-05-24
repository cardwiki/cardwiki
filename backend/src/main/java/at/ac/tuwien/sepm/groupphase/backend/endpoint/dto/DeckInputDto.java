package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

@Data
public class DeckInputDto {
    @NotBlank
    @Length(max = Deck.MAX_NAME_LENGTH)
    private String name;
}
