package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    /**
     * Create a new image
     *
     * @param file to save
     * @return created image
     */
    Image create(MultipartFile file);

    /**
     * Find a single image by id.
     *
     * @param filename the filename of the image entry
     * @return the image entry
     */
    Image findByFilename(String filename);

}
