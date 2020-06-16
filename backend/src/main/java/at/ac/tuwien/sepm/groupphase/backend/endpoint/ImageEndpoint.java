package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "api/v1/images")
public class ImageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ImageService imageService;
    private final Path imageServedPath;

    @Autowired
    public ImageEndpoint(ImageService imageService, @Value("${cawi.image-served-path}") String imageServedPath) {
        this.imageService = imageService;
        this.imageServedPath = Paths.get(imageServedPath);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Save a new image", authorizations = {@Authorization(value = "ROLE_USER")})
    public String upload(@RequestParam MultipartFile file) {
        LOGGER.info("POST api/v1/images");
        return imageServedPath.resolve(imageService.save(file)).toString();
    }
}
