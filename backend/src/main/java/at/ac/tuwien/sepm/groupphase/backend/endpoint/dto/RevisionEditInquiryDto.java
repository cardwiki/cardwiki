package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import com.google.common.base.Objects;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RevisionEditInquiryDto {

    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    private String textFront;

    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    private String textBack;

    private String imageFront;

    private String imageBack;
}