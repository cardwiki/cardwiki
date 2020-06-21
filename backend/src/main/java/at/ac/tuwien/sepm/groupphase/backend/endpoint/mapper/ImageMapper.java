package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Paths;

@Mapper
public abstract class ImageMapper {

    @Value("${cawi.image-served-path}")
    String imageServedPath;

    abstract public ImageDto imageToImageDto(Image image);

    @AfterMapping
    protected void filenameToUrl(@MappingTarget ImageDto imageDto) {
        imageDto.setUrl(Paths.get(imageServedPath, imageDto.getFilename()).toString());
    }
}
