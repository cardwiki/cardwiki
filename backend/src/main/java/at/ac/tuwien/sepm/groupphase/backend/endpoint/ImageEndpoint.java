package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ImageMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "api/v1/images")
public class ImageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ImageService imageService;
    private final ImageMapper imageMapper;

    @Autowired
    public ImageEndpoint(ImageService imageService, ImageMapper imageMapper) {
        this.imageService = imageService;
        this.imageMapper = imageMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Save a new image", authorizations = {@Authorization("user")})
    public ImageDto upload(@RequestParam MultipartFile file) {
        LOGGER.info("POST api/v1/images filename: {}", file.getOriginalFilename());
        return imageMapper.imageToImageDto(imageService.create(file));
    }
}
