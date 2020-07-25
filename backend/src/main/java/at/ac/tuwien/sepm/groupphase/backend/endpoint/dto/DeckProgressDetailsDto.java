package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class DeckProgressDetailsDto {
    private Long deckId;
    private String deckName;
    private DeckProgressDto normal;
    private DeckProgressDto reverse;
}
