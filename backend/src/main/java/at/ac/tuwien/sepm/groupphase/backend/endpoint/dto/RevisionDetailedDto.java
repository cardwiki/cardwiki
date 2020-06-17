package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RevisionDetailedDto {

    private Long id;
    private Revision.Type type;
    private String message;
    private LocalDateTime createdAt;
    private CardSimpleDto card;
}
