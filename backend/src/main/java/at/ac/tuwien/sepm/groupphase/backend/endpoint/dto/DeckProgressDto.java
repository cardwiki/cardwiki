package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DeckProgressDto {
    // total number of cards to learn
    private int totalCount;

    // cards without progress
    private int newCount;

    // cards in learning mode
    private int learningCount;

    // total number of cards which are due
    private int dueCount;
}
