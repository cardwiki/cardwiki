package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDto {
    @NotNull
    @Pattern(regexp = "[a-z0-9_-]+")
    private String username;

    @NotNull
    private String description;

    private boolean isAdmin;
}
