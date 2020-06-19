package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
        assertThrows(NotFoundException.class, () -> userService.findUserByIdOrThrow(id));
    }

    @Test
    public void givenNothing_whenFindOneExistent_thenReturnUser() {
        Long id = 1L;
        User user = getSampleUser();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertEquals(user, userService.findUserByIdOrThrow(id));
    }

    @Test
    public void givenNothing_whenFindOneArgNull_thenThrowNullPointer() {
        assertThrows(UserNotFoundException.class, () -> userService.findUserByIdOrThrow(null));
    }

    @Test
    public void givenNothing_whenSearchByNameArgNull_thenThrowNullPointer() {
        assertThrows(NullPointerException.class, () -> userService.searchByUsername(null, Pageable.unpaged()));
    }

    @Test
    public void givenNothing_whenSearchByNameNotExistent_thenReturnEmptyList() {
        Mockito.when(userRepository.findByUsernameContainingIgnoreCase("", Pageable.unpaged()))
            .thenReturn(new PageImpl<>(Collections.emptyList()));
        assertTrue(userService.searchByUsername("", Pageable.unpaged()).isEmpty());
    }

    @Test
    public void givenNothing_whenSearchByNameExistent_thenReturnUser() {
        User user = mock(User.class);
        Mockito.when(userRepository.findByUsernameContainingIgnoreCase("foo", Pageable.unpaged()))
            .thenReturn(new PageImpl<>(Collections.singletonList(user)));
        assertTrue(userService.searchByUsername("foo", Pageable.unpaged()).getContent().contains(user));
    }

    @Test
    public void givenNothing_whenAttemptRegisterAndRepositoryThrowsConstraintViolationException_thenThrow() {
        User user = getSampleUser();
        Mockito.when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("notImportantForTest", new ConstraintViolationException("notImportantForTest", new SQLException(), "some constraint")));
        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(user));
    }

    @Test
    public void givenNothing_whenAttemptRegisterAndRepositoryThrowsOtherException_thenThrow() {
        User user = getSampleUser();
        Mockito.when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("notImportantForTest", new Exception()));
        assertThrows(Exception.class, () -> userService.createUser(user));
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
