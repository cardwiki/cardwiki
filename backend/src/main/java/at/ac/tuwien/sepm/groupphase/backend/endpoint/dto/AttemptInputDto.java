package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttemptInputDto {
    @NotNull
    private Long cardId;
    @NotNull
    private Status status;

    public enum Status {
        AGAIN,
        GOOD,
        EASY
    }
}
