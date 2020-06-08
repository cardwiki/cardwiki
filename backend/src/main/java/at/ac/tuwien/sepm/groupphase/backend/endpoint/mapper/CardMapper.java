package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardContentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface CardMapper {

    @Mapping(source = "card.latestRevision.revisionEdit.textFront", target = "textFront")
    @Mapping(source = "card.latestRevision.revisionEdit.textBack", target = "textBack")
    CardDetailsDto cardToCardDetailsDto(Card card);

    @Mapping(source = "card.latestRevision.revisionEdit.textFront", target = "textFront")
    @Mapping(source = "card.latestRevision.revisionEdit.textBack", target = "textBack")
    CardSimpleDto cardToCardSimpleDto(Card card);

    @Mapping(source = "revision.createdBy.id", target = "createdBy")
    RevisionSimpleDto revisionToRevisionSimpleDto(Revision revision);

    @Mapping(source = "textFront", target="revisionEdit.textFront")
    @Mapping(source = "textBack", target="revisionEdit.textBack")
    Revision revisionInputDtoToRevision(RevisionInputDto revisionInputDto);

    @Named("cardToCardContentDto")
    @Mapping(source = "card.latestRevision.revisionEdit.textFront", target = "textFront")
    @Mapping(source = "card.latestRevision.revisionEdit.textBack", target = "textBack")
    CardContentDto cardToCardContentDto(Card card);

    @IterableMapping(qualifiedByName = "cardToCardContentDto")
    List<CardContentDto> cardToCardContentDto(List<Card> cards);
}

