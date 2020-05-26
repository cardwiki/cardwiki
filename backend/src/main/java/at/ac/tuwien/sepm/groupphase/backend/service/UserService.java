package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    /**
     * Loads a user using the userId
     *
     * @param id of the user
     * @return the user entity
     * @throws UserNotFoundException is thrown if the specified user does not exists
     */
    User loadUserById(long id);

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
    User loadUserByUsername(String username);

    /**
     * Loads an user for an Auth ID.
     * @param authId
     * @return the user entity
     * @throws UserNotFoundException if the specified user does not exist
     */
    User loadUserByAuthId(String authId);

    /**
     * Loads the currently authenticated user.
     * @return the user entity
     * @throws UserNotFoundException if the current user is not stored in the repository
     */
    User loadCurrentUser();

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
    List<Deck> getDecks(long id, Pageable pageable);

    /**
     * Loads revisions created by user using their id
     *
     * @param id of the user to search revisions for
     * @param pageable pagination data consisting of LIMIT and OFFSET
     * @return List of Revisions created by the user
     */
    List<Revision> getRevisions(long id, Pageable pageable);

    /**
     * Change settings of user with id {@code id}
     *
     * @param id of user to edit
     * @param user contains user data to edit
     * @return User with changed settings
     */
    User editSettings(long id, User user);
}
