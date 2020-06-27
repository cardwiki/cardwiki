package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionCreate;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Paths;

@Mapper
abstract public class RevisionMapper {

    @Mapping(source = "revision.card.id", target = "cardId")
    @Mapping(source = "revision.card.deck", target = "deck")
    public abstract RevisionDtoWithDeck revision_to_revisionDtoWithDeck(Revision revision);

    @Mapping(source = "revision.createdBy", target = "createdBy")
    @Mapping(source = "revision.card.id", target = "cardId")
    abstract RevisionDtoWithContent _revision_to_revisionDtoWithContent(Revision revision);

    @Mapping(source = "revision.createdBy", target = "createdBy")
    @Mapping(source = "revision.card.id", target = "cardId")
    @Mapping(source = "revision.imageFront.filename", target = "imageFront.url", qualifiedByName = "prefixImagesServedPath")
    @Mapping(source = "revision.imageBack.filename", target = "imageBack.url", qualifiedByName = "prefixImagesServedPath")
    abstract RevisionDtoWithContent _revisionEdit_to_revisionDtoWithContent(RevisionEdit revision);

    public RevisionDtoWithContent revisionToRevisionDetailedDto(Revision revision){
        if (revision instanceof RevisionEdit) {
            return _revisionEdit_to_revisionDtoWithContent((RevisionEdit) revision);
        } else
            return _revision_to_revisionDtoWithContent(revision);
    }

    @Mapping(source = "edit.card.id", target = "id")
    @Mapping(source = "edit.imageFront.filename", target = "imageFrontUrl", qualifiedByName = "prefixImagesServedPath")
    @Mapping(source = "edit.imageBack.filename", target = "imageBackUrl", qualifiedByName = "prefixImagesServedPath")
    public abstract CardContentDto revisionEditToCardContentDto(RevisionEdit edit);

    @Mapping(source = "edit.imageFrontFilename", target = "imageFront", qualifiedByName = "filenameToImage")
    @Mapping(source = "edit.imageBackFilename", target = "imageBack", qualifiedByName = "filenameToImage")
    public abstract RevisionEdit revisionEditDtoToRevisionEdit(RevisionInputDto edit);

    @Mapping(source = "edit.imageFrontFilename", target = "imageFront", qualifiedByName = "filenameToImage")
    @Mapping(source = "edit.imageBackFilename", target = "imageBack", qualifiedByName = "filenameToImage")
    public abstract RevisionCreate revisionEditDtoToRevisionCreate(RevisionInputDto edit);

    @Mapping(source = "edit.card.id", target = "id")
    @Mapping(source = "edit.card.deck", target = "deck")
    @Mapping(source = "edit.imageFront.filename", target = "imageFrontUrl", qualifiedByName = "prefixImagesServedPath")
    @Mapping(source = "edit.imageBack.filename", target = "imageBackUrl", qualifiedByName = "prefixImagesServedPath")
    public abstract CardSimpleDto revisionEditToCardSimpleDto(RevisionEdit edit);

    @Mapping(source = "edit.imageFront.filename", target = "imageFront.filename")
    @Mapping(source = "edit.imageBack.filename", target = "imageBack.filename")
    @Mapping(source = "edit.imageFront.filename", target = "imageFront.url", qualifiedByName = "prefixImagesServedPath")
    @Mapping(source = "edit.imageBack.filename", target = "imageBack.url", qualifiedByName = "prefixImagesServedPath")
    public abstract CardUpdateDto revisionEditToCardUpdateDto(RevisionEdit edit);

    @Value("${cawi.image-served-path}")
    String imageServedPath;

    @Named("filenameToImage")
    public static Image filenameToImage(String filename) {
        if (filename == null)
            return null;
        Image image = new Image();
        image.setFilename(filename);
        return image;
    }

    @Named("prefixImageServedPath")
    public String prefixImageServedPath(String filename){
        if (filename == null)
            return null;
        return Paths.get(imageServedPath, filename).toString();
    }

}

