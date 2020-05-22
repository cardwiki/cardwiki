package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryDetailedDto extends CategorySimpleDto {

    private Long createdBy;
    private CategorySimpleDto parent;
    private List<CategorySimpleDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DeckSimpleDto> decks;

}
