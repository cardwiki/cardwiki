package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Find all users containing {@code name} (case insensitive)
     *
     * @param name the search string
     * @param pageable the paging parameters
     * @return ordered list of all users with names containing {@code name}
     */
    List<User> findByUsernameContainingIgnoreCaseAndDeletedFalse(String name, Pageable pageable);

    Optional<User> findByUsername(String username);
    Optional<User> findByAuthId(String authId);
}
