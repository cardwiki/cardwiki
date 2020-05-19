package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardContentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionEditInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
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


    @Mapping(source = "revision.createdBy.username", target = "createdBy")
    RevisionSimpleDto revisionToRevisionSimpleDto(Revision revision);


    RevisionEdit revisionEditInquiryDtoToRevisionEdit(RevisionEditInquiryDto revisionEditInquiryDto);


    @Named("cardToCardContentDto")
    @Mapping(source = "card.latestRevision.revisionEdit.textFront", target = "textFront")
    @Mapping(source = "card.latestRevision.revisionEdit.textBack", target = "textBack")
    CardContentDto cardToCardContentDto(Card card);

    @IterableMapping(qualifiedByName = "cardToCardContentDto")
    List<CardContentDto> cardToCardContentDto(List<Card> cards);
}

