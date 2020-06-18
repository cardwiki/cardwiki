package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionCreate;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface RevisionMapper {

    @Mapping(source = "revision.createdBy.id", target = "createdBy")
    RevisionSimpleDto revisionToRevisionSimpleDto(Revision revision);

    @Mapping(source = "revision.card", target = "card")
    RevisionDetailedDto revisionToRevisionDetailedDto(Revision revision);

    List<RevisionSimpleDto> revisionsToRevisionSimpleDtoList(List<Revision> revisions);

    CardContentDto revisionEditToCardContentDto(RevisionEdit edit);

    RevisionEdit revisionEditDtoToRevisionEdit(RevisionEditDto dto);

    RevisionCreate revisionEditDtoToRevisionCreate(RevisionEditDto dto);

    @Mapping(source = "revision.card.id", target = "id")
    @Mapping(source = "revision.card.deck", target = "deck")
    CardSimpleDto revisionEditToCardSimpleDto(RevisionEdit revision);
}

