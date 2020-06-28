package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class RevisionDto {
    private Long id;
    private Long cardId;
    private String message;
    private LocalDateTime createdAt;
    private Revision.Type type;
    private UserSimpleDto createdBy;
}
