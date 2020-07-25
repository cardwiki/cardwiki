package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.InheritConfiguration;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Mapper(uses = ImageMapper.class)
public interface UserMapper {
    User userInputDtoToUser(UserInputDto userInputDto);
    UserDetailsDto userToUserDetailsDto(User user);
    User userUpdateDtoToUser(UserUpdateDto userUpdateDto);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "revisions", target = "revisions", qualifiedByName = "revisionsToRevisionExportDtoList")
    UserExportDto userToUserExportDto(User user);

    @Mapping(source = "progress.id.reverse", target = "reverse")
    @Mapping(source = "progress.id.card.id", target = "cardId")
    @Mapping(source = "progress.id.card.latestRevision", target = "latestRevision", qualifiedByName = "revisionToRevisionExportDto")
    ProgressExportDto progressToProgressExportDto(Progress progress);

    @Mapping(source = "revision.card.id", target = "cardId")
    @Mapping(source = "revision.card.deck", target = "deck")
    RevisionExportDto _revisionToRevisionExportDto(Revision revision);

    @InheritConfiguration(name = "_revisionToRevisionExportDto")
    RevisionExportDto _revisionEditToRevisionExportDto(RevisionEdit revision);

    @Named("revisionToRevisionExportDto")
    // Using Object to prevent ambigous method error
    default RevisionExportDto _anyRevisionToRevisionExportDto(Object revision) {
        if (revision == null) {
            return null;
        }

        if (revision instanceof RevisionEdit) {
            return _revisionEditToRevisionExportDto((RevisionEdit) revision);
        } else if (revision instanceof Revision) {
            return _revisionToRevisionExportDto((Revision) revision);
        } else {
            throw new RuntimeException("_revisionToRevisionExportDto may only be called with revisions");
        }
    }

    @Named("revisionsToRevisionExportDtoList")
    default List<RevisionExportDto> revisionSetToRevisionExportDtoList(Set<Revision> set) {
        if (set == null) {
            return null;
        }

        List<RevisionExportDto> list = new ArrayList<RevisionExportDto>(set.size());
        for (Revision revision : set) {
            list.add(_anyRevisionToRevisionExportDto(revision));
        }

        return list;
    }
}
