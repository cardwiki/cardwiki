package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpClientErrorException;

public interface UserService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <p>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param username the username
     * @return the user entity
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    ApplicationUser loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Loads an user for an OAuth ID.
     * @param oauthId
     * @return the user entity
     */
    ApplicationUser loadUserByOauthId(String oauthId) throws NotFoundException;

    /**
     * Register a new user.
     * @param oauthId
     * @param username
     * @return the new user entity
     */
    ApplicationUser registerUser(String oauthId, String username);
}
