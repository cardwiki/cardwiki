package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

    /**
     * Find image with filename
     *
     * @param filename of the image
     * @return image with passed filename
     */
    Optional<Image> findByFilename(String filename);

    /**
     * Find all images created by user for exporting data
     *
     * @param userId id of the user
     * @return all categories by user
     */
    Set<Image> findExportByCreatedBy_Id(Long userId);
}
