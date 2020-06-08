package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class CardSimpleDto {

    private Long id;
    private DeckSimpleDto deck;
    private String textFront;
    private String textBack;
    private String imageFront;
    private String imageBack;
}
