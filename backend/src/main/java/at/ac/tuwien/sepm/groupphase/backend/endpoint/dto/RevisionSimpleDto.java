package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.google.common.base.Objects;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RevisionSimpleDto {

    private Long id;
    private String message;
    private Long createdBy;
    private LocalDateTime createdAt;
}
