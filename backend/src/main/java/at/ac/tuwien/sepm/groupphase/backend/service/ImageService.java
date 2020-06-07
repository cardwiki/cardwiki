package at.ac.tuwien.sepm.groupphase.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    /**
     * Saves a new image
     *
     * @param image to save
     */
    String save(byte[] image);

    /**
     * Get an image by filename
     *
     * @param filename of the image
     * @return image
     */
    byte[] getImage(String filename);
}
