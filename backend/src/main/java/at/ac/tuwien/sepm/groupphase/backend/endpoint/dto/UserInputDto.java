package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDto {
    @NotNull
    @Pattern(regexp = "[a-z0-9_-]+")
    @Length(max = User.MAX_USERNAME_LENGTH)
    private String username;

    @NotNull
    @Length(max = User.MAX_DESCRIPTION_LENGTH)
    private String description;

    private boolean isAdmin;
}
