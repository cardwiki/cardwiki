package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.validation.ContentNotNull;
import at.ac.tuwien.sepm.groupphase.backend.validation.NullOrNotBlank;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import lombok.Data;
import javax.validation.constraints.Size;

@ContentNotNull
@Data
public class RevisionInputDto {

    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    @NullOrNotBlank
    private String textFront;

    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    @NullOrNotBlank
    private String textBack;

    private ImageDto imageFront;

    private ImageDto imageBack;

    @Size(max = Revision.MAX_MESSAGE_SIZE)
    private String message;
}