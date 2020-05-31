package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserOutputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Register the authenticated user")
    public UserOutputDto register(Authentication token, @Valid @RequestBody UserInputDto userInputDto) {
        if (token == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not authenticated");

        User u = userMapper.userInputDtoToUser(userInputDto);
        u.setAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        u.setEnabled(true);
        u = userService.createUser(u);
        return userMapper.userToUserOutputDto(u);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @ApiOperation(value = "List all users")
    public List<UserOutputDto> get() {
        return userService.getAll().stream().map(user -> userMapper.userToUserOutputDto(user)).collect(Collectors.toList());
    }
}
