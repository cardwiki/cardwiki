package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class UserDetailsDto {
    private Long id;

    private String username;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isAdmin;

    private boolean isEnabled;

    private boolean isDeleted;
}
