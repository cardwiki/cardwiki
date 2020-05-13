package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class SimpleMessageDto {
    private Long id;

    private LocalDateTime publishedAt;

    private String title;

    private String summary;
}