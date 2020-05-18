package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface RevisionMapper {

    @Mapping(source = "revision.createdBy.id", target = "createdBy")
    RevisionSimpleDto revisionToRevisionSimpleDto(Revision revision);

    List<RevisionSimpleDto> revisionsToRevisionSimpleDtoList(List<Revision> revisions);
}

