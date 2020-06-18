package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Find image with filename
     *
     * @param filename of the image
     * @return image with passed filename
     */
    Image findByFilename(String filename);
}
