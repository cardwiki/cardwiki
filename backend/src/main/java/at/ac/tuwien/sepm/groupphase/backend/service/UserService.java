package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {

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
     */
    Optional<User> loadUserByAuthId(String authId);

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
}
