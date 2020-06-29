package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @Length(max = User.MAX_DESCRIPTION_LENGTH)
    private String description;

    private Boolean admin;

    private Boolean enabled;

    private String reason;

    private String theme;
}
