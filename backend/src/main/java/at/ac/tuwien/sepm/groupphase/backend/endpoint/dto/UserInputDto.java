package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDto {
    @NotNull
    private String oAuthId;

    @NotNull
    private String username;

    @NotNull
    private String description;

    @NotNull
    private boolean isAdmin;
}
