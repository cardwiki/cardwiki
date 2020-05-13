package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Data
public class CategoryDetailedDto extends CategorySimpleDto {

    private User createdBy;
    private Category parent;
    private Set<Category> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
