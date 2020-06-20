package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.AuthenticationRequiredException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Loads a user using the userId
     *
     * @param id of the user
     * @return the user entity
     * @throws UserNotFoundException is thrown if the specified user does not exists
     */
    User findUserByIdOrThrow(Long id);

    /**
     * Find a user in the context of Spring Security based on the email address
     * <p>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param username the username
     * @return the user entity
     * @throws UserNotFoundException is thrown if the specified user does not exists
     */
    User findUserByUsernameOrThrow(String username);

    /**
     * Loads an user for an Auth ID.
     * @param authId authorization ID of the user to load
     * @return the user entity
     */
    Optional<User> findUserByAuthId(String authId);

    /**
     * Loads the currently authenticated user.
     * @return the user entity
     * @throws AuthenticationRequiredException if no authentication is provided or the current user is not stored in the repository
     */
    User loadCurrentUserOrThrow();

    /**
     * Create a new user.
     * @param user to create
     * @return the new user entity
     */
    User createUser(User user);

    /**
     * @return all users
     */
    List<User> getAll();

    /**
     * Find all users containing {@code username} (case insensitive)
     *
     * @param username the search string
     * @param pageable the paging parameters
     * @return ordered list of all users with usernames containing {@code username}
     */
    List<User> searchByUsername(String username, Pageable pageable);

    /**
     * Loads decks created by user using their id
     *
     * @param id of the user to search decks for
     * @param pageable pagination data consisting of LIMIT and OFFSET
     * @return List of Decks created by the user
     */
    List<Deck> getDecks(Long id, Pageable pageable);

    /**
     * Loads revisions created by user using their id
     *
     * @param id of the user to search revisions for
     * @param pageable pagination data consisting of LIMIT and OFFSET
     * @return List of Revisions created by the user
     */
    List<Revision> getRevisions(Long id, Pageable pageable);

    /**
     * Change settings of user with id {@code id}
     *
     * @param id of user to edit
     * @param user contains user data to edit
     * @return User with changed settings
     */
    User editSettings(Long id, User user);

    /**
     * Updates a user.
     *
     * @param id of the user to be updated.
     * @param user containing the data to update the user with
     * @return the updated user
     * @throws UserNotFoundException if the user to be updated does not exist.
     * @throws AccessDeniedException if the update operation is not allowed.
     */
    User updateUser(Long id, User user);

    /**
     * Deletes a user.
     *
     * @param id of the user to be deleted.
     * @throws AccessDeniedException if the user to be deleted is an admin.
     * @throws UserNotFoundException if the user to be deleted does not exist.
     */
    void delete(Long id);
}
