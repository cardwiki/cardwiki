package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RevisionDetailedDto {

    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private CardSimpleDto card;
}
