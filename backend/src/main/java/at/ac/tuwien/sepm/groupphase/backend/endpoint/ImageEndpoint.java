package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    private final Path path;

    @Autowired
    public ImageEndpoint(ImageService imageService, @Value("${cawi.image-served-path}") String path) {
        this.imageService = imageService;
        this.path = Paths.get(path);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @ApiOperation(value = "Save a new image")
    public String upload(@RequestParam MultipartFile file) {
        LOGGER.info("POST api/v1/images");
        return path.resolve(imageService.save(file)).toString();
    }
}
