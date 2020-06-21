package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import org.springframework.web.multipart.MultipartFile;
import at.ac.tuwien.sepm.groupphase.backend.exception.ImageNotFoundException;

public interface ImageService {

    /**
     * Create a new image
     *
     * @param file to save
     * @return created image
     */
    Image create(MultipartFile file);

    /**
     * Find a single image by filename.
     *
     * @param filename of the image
     * @return the image entry
     * @throws ImageNotFoundException if no image with this filename exists
     */
    Image findOneOrThrow(String filename);
}
