package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentSimpleDto {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserSimpleDto createdBy;
}
