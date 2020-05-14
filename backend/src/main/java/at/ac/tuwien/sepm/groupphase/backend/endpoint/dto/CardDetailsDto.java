package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.google.common.base.Objects;
import lombok.Data;

import java.util.List;

@Data
public class CardDetailsDto {

    private Long id;
    private DeckSimpleDto deck;
    private List<RevisionSimpleDto> revisions;
    private String textFront;
    private String textBack;
}
