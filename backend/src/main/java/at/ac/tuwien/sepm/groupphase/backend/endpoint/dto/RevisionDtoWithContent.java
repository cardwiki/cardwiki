package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class RevisionDtoWithContent extends RevisionDto {
    private String textFront;
    private String textBack;
    private ImageDto imageFront;
    private ImageDto imageBack;
}
