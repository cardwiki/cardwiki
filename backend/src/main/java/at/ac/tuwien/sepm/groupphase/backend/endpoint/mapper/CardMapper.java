package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Mapper
abstract public class CardMapper {

    @Value("${cawi.image-served-path}")
    String imageServedPath;

    @Mapping(source = "card.latestRevision.revisionEdit.textFront", target = "textFront")
    @Mapping(source = "card.latestRevision.revisionEdit.textBack", target = "textBack")
    @Mapping(source = "card.latestRevision.revisionEdit.imageFront", target = "imageFront")
    @Mapping(source = "card.latestRevision.revisionEdit.imageBack", target = "imageBack")
    abstract public CardDetailsDto cardToCardDetailsDto(Card card);

    @Mapping(source = "card.latestRevision.revisionEdit.textFront", target = "textFront")
    @Mapping(source = "card.latestRevision.revisionEdit.textBack", target = "textBack")
    @Mapping(source = "card.latestRevision.revisionEdit.imageFront", target = "imageFront")
    @Mapping(source = "card.latestRevision.revisionEdit.imageBack", target = "imageBack")
    abstract public CardSimpleDto cardToCardSimpleDto(Card card);

    @Mapping(source = "revision.createdBy.id", target = "createdBy")
    abstract public RevisionSimpleDto revisionToRevisionSimpleDto(Revision revision);

    abstract public RevisionEdit revisionEditInquiryDtoToRevisionEdit(RevisionEditInquiryDto revisionEditInquiryDto);

    @Mapping(source = "card.latestRevision.revisionEdit.textFront", target = "textFront")
    @Mapping(source = "card.latestRevision.revisionEdit.textBack", target = "textBack")
    @Mapping(source = "card.latestRevision.revisionEdit.imageFront", target = "imageFront")
    @Mapping(source = "card.latestRevision.revisionEdit.imageBack", target = "imageBack")
    abstract public CardContentDto cardToCardContentDto(Card card);

    abstract public List<CardContentDto> cardToCardContentDto(List<Card> cards);

    @AfterMapping
    protected void filenameToUrl(@MappingTarget ImageDto imageDto) {
        imageDto.setUrl(Paths.get(imageServedPath, imageDto.getFilename()).toString());
    }
}
