package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardContentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.nio.file.Paths;
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

    @Mapping(source = "textFront", target="revisionEdit.textFront")
    @Mapping(source = "textBack", target="revisionEdit.textBack")
    @Mapping(source = "imageFront", target = "revisionEdit.imageFront")
    @Mapping(source = "imageBack", target = "revisionEdit.imageBack")
    abstract public Revision revisionInputDtoToRevision(RevisionInputDto revisionInputDto);

    @Named("cardToCardContentDto")
    @Mapping(source = "card.latestRevision.revisionEdit.textFront", target = "textFront")
    @Mapping(source = "card.latestRevision.revisionEdit.textBack", target = "textBack")
    @Mapping(source = "card.latestRevision.revisionEdit.imageFront", target = "imageFront")
    @Mapping(source = "card.latestRevision.revisionEdit.imageBack", target = "imageBack")
    abstract public CardContentDto cardToCardContentDto(Card card);

    @IterableMapping(qualifiedByName = "cardToCardContentDto")
    abstract public List<CardContentDto> cardToCardContentDto(List<Card> cards);

    @AfterMapping
    protected void filenameToUrl(@MappingTarget ImageDto imageDto) {
        imageDto.setUrl(Paths.get(imageServedPath, imageDto.getFilename()).toString());
    }
}
