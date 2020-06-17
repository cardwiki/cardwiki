package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class ImageDto {

    private Long id;
    private String filename;
    private String url;
}
