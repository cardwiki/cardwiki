package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, Long> {
}
