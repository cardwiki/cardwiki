package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RevisionDetailedDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserOutputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DeckMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RevisionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    @Autowired
    private DeckMapper deckMapper;

    @Autowired
    private RevisionMapper revisionMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Register the authenticated user")
    public UserOutputDto register(Authentication token, @Valid @RequestBody UserInputDto userInputDto) {
        if (token == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not authenticated");
        User u = userMapper.userInputDtoToUser(userInputDto);
        u.setAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        u = userService.createUser(u);
        return userMapper.userToUserOutputDto(u);
    }

    /*
    @Secured("ROLE_USER")
    @GetMapping
    @ApiOperation(value = "List all users")
    public List<UserOutputDto> get() {
        return userService.getAll().stream().map(user -> userMapper.userToUserOutputDto(user)).collect(Collectors.toList());
    }
     */

    @GetMapping
    @ApiOperation(value = "Search for users")
    public List<UserOutputDto> search(@RequestParam String username, @RequestParam Integer limit, @RequestParam Integer offset) {
        return userService.searchByUsername(username, PageRequest.of(offset, limit))
            .stream()
            .map(userMapper::userToUserOutputDto)
            .collect(Collectors.toList());
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/@me/profile")
    @ApiOperation(value = "Get user profile of logged in user")
    public UserOutputDto getProfile() {
        return userMapper.userToUserOutputDto(userService.loadCurrentUser());
    }

    @GetMapping(value = "/{username}/profile")
    @ApiOperation(value = "Get user profile")
    public UserOutputDto getProfile(@PathVariable String username) {
        return userMapper.userToUserOutputDto(userService.loadUserByUsername(username));
    }

    @GetMapping(value = "/{id}/decks")
    @ApiOperation(value = "Get decks created by user")
    public List<DeckSimpleDto> getDecks(@PathVariable long id, @RequestParam Integer limit, @RequestParam Integer offset) {
        return userService.getDecks(id, PageRequest.of(offset, limit)).stream().map(deckMapper::deckToDeckSimpleDto).collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}/revisions")
    @ApiOperation(value = "Get revisions created by user")
    public List<RevisionDetailedDto> getRevisions(@PathVariable long id, @RequestParam Integer limit, @RequestParam Integer offset) {
        return userService.getRevisions(id, PageRequest.of(offset, limit)).stream().map(revision -> revisionMapper.revisionToRevisionDetailedDto(revision)).collect(Collectors.toList());
    }

    @Secured("ROLE_USER")
    @PostMapping(value = "/description")
    @ApiOperation(value = "Change description of logged in user")
    public UserOutputDto editDescription(@Valid  @RequestBody String description) {
        return userMapper.userToUserOutputDto(userService.editDescription(userService.loadCurrentUser().getId(), description));
    }
}
