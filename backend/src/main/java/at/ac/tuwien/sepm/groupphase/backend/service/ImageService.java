package at.ac.tuwien.sepm.groupphase.backend.service;


import java.io.InputStream;

public interface ImageService {

    /**
     * Saves a new image
     *
     * @param image to save
     */
    String save(InputStream image);

}
