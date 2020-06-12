package at.ac.tuwien.sepm.groupphase.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    /**
     * Saves a new image
     *
     * @param file to save
     * @return filename of saved image
     */
    String save(MultipartFile file);

}
