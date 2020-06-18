package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RevisionSimpleDto {

    private Long id;
    private String message;
    private Long createdBy;
    private LocalDateTime createdAt;
    private String type;
}
