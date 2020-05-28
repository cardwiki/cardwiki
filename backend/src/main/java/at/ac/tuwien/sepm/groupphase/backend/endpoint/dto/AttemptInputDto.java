package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class AttemptInputDto {
    private Long cardId;
    private Status status;

    public enum Status {
        AGAIN,
        GOOD,
        EASY
    }
}
