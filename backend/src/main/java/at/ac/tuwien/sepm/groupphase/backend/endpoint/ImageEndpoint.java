package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "api/v1/images")
public class ImageEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ImageService imageService;

    @Autowired
    public ImageEndpoint(ImageService imageService) {
        this.imageService = imageService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = {"image/png", "image/jpg", "image/jpeg"})
    @ApiOperation(value = "Save a new image")
    public String upload(@RequestBody byte[] image) {
        LOGGER.info("POST api/v1/images");
        return imageService.save(image);
    }
}
