package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class RevisionExportDto extends RevisionDto {
    private DeckSimpleDto deck;
    private String textFront;
    private String textBack;
    private ImageDto imageFront;
    private ImageDto imageBack;
}
