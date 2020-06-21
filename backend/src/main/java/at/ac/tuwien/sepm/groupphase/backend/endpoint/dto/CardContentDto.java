package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class CardContentDto {

    private Long id;
    private String textFront;
    private String textBack;
    private String imageFrontUrl;
    private String imageBackUrl;
}