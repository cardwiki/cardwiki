package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RevisionEditDto {

    @NotNull(message = "Front text must not be null")
    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    @NotBlank
    private String textFront;

    @NotNull(message = "Back text must not be null")
    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    @NotBlank
    private String textBack;

    @Size(max = Revision.MAX_MESSAGE_SIZE)
    private String message;
}