package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeckIdDto {
    @NotNull
    private Long deckId;
}
