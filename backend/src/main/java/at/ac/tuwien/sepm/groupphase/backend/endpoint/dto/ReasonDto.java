package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ReasonDto {
    @Size(max = Revision.MAX_MESSAGE_SIZE)
    private String message;
}
