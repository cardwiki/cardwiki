package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.validation.NullOrNotBlank;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import lombok.Data;
import javax.validation.constraints.Size;

@Data
public class RevisionEditDto {

    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    @NullOrNotBlank
    private String textFront;

    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    @NullOrNotBlank
    private String textBack;

    @NullOrNotBlank
    private String imageFrontFilename;

    @NullOrNotBlank
    private String imageBackFilename;

    @Size(max = Revision.MAX_MESSAGE_SIZE)
    private String message;
}