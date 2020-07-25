package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DeckProgressDto {
    private int newCount;
    private int learningCount;
    private int toReviewCount;
}
