package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeckDto {
    private Long id;
    private String name;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategorySimpleDto> categories;
    private List<CardContentDto> cards;
}
