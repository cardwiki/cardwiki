package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class RevisionDtoWithContent extends RevisionDto {
    private String textFront;
    private String textBack;
    private String imageFront;
    private String imageBack;
    private UserSimpleDto createdBy;
}
