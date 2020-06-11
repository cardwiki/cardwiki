package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest extends TestDataGenerator {
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void givenNothing_whenFindOneNonexistent_thenThrowNotFoundException() {
        Long id = 1L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.loadUserById(id));
    }

    @Test
    public void givenNothing_whenFindOneExistent_thenReturnUser() {
        Long id = 1L;
        User user = getSampleUser();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertEquals(user, userService.loadUserById(id));
    }

    @Test
    public void givenNothing_whenFindOneArgNull_thenThrowNullPointer() {
        assertThrows(UserNotFoundException.class, () -> userService.loadUserById(null));
    }

    @Test
    public void givenNothing_whenSearchByNameArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> userService.searchByUsername(null, Pageable.unpaged()));
    }

    @Test
    public void givenNothing_whenSearchByNameNotExistent_thenReturnEmptyList() {
        Mockito.when(userRepository.findByUsernameContainingIgnoreCase("", Pageable.unpaged()))
            .thenReturn(Collections.emptyList());
        assertTrue(userService.searchByUsername("", Pageable.unpaged()).isEmpty());
    }

    @Test
    public void givenNothing_whenSearchByNameExistent_thenReturnUser() {
        User user = getSampleUser();
        Mockito.when(userRepository.findByUsernameContainingIgnoreCase(user.getUsername(), Pageable.unpaged()))
            .thenReturn(Collections.singletonList(user));
        assertTrue(userService.searchByUsername(user.getUsername(), Pageable.unpaged()).contains(user));
    }

    @Test
    public void givenMultipleUsers_whenGetAll_thenReturnAllUsers() {
        User user1 = getSampleUser();
        User user2 = getSampleUser();
        User user3 = getSampleUser();
        Mockito.when(userRepository.findAll())
            .thenReturn(Arrays.asList(user1, user2, user3));
        assertEquals(Arrays.asList(user1, user2, user3), userService.getAll());
    }

    @Test
    public void givenUser_whenUpdateUser_thenReturnUpdatedUser() {
        User user = getSampleUser();
        user.setAdmin(!user.isAdmin());
        user.setEnabled(!user.isEnabled());
        user.setDescription("updated " + user.getDescription());
        user.setUsername("newusername");

        Mockito.when(userRepository.saveAndFlush(user))
            .thenReturn(user);
        assertEquals(user, userService.updateUser(user.getId(), user));
    }
}
