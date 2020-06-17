package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentInquiryDto {
    @NotBlank
    @Size(max = Comment.MAX_MESSAGE_LENGTH)
    private String message;
}