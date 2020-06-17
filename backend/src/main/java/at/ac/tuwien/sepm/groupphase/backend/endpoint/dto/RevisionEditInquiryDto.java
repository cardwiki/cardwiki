package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.validation.ContentNotNull;
import at.ac.tuwien.sepm.groupphase.backend.validation.NullOrNotBlank;
import lombok.Data;
import javax.validation.constraints.Size;

@ContentNotNull
@Data
public class RevisionEditInquiryDto {

    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    @NullOrNotBlank
    private String textFront;

    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    @NullOrNotBlank
    private String textBack;

    private Image imageFront;

    private Image imageBack;
}