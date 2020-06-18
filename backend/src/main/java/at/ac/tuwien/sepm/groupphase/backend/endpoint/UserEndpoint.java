package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DeckMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RevisionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.service.FavoriteService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeckMapper deckMapper;

    @Autowired
    private RevisionMapper revisionMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Register the authenticated user")
    public UserDetailsDto register(Authentication token, @Valid @RequestBody UserInputDto userInputDto) {
        if (token == null)
            throw new AccessDeniedException("Login using an Oauth2 provider first");

        User u = userMapper.userInputDtoToUser(userInputDto);
        u.setAuthId(SecurityContextHolder.getContext().getAuthentication().getName());
        u.setEnabled(true);
        u = userService.createUser(u);
        return userMapper.userToUserDetailsDto(u);
    }

    @GetMapping
    @ApiOperation(value = "Search for users")
    public List<UserDetailsDto> search(@Valid @NotNull @RequestParam String username, @RequestParam Integer limit, @RequestParam Integer offset) {
        return userService.searchByUsername(username, PageRequest.of(offset, limit, Sort.by("username").ascending()))
            .stream()
            .map(userMapper::userToUserDetailsDto)
            .collect(Collectors.toList());
    }

    @GetMapping(value = "/byname/{username}")
    @ApiOperation(value = "Get user profile")
    public UserDetailsDto getProfile(@PathVariable String username) {
        return userMapper.userToUserDetailsDto(userService.findUserByUsernameOrThrow(username));
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
    @PutMapping(value = "/{userId}/favorites/{deckId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add deck to user favorites")
    public DeckSimpleDto addFavorite(@PathVariable Long userId, @PathVariable Long deckId) {
        return deckMapper.deckToDeckSimpleDto(favoriteService.addFavorite(userId, deckId));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{userId}/favorites")
    @ApiOperation(value = "Get favorites of user")
    public Page<DeckSimpleDto> getFavorites(@PathVariable Long userId, @RequestParam Integer limit, @RequestParam Integer offset) {
        LOGGER.info("GET /api/v1/users/{}/favorites?limit={}&offset={}", userId, limit, offset);
        return favoriteService.getFavorites(userId, PageRequest.of(offset, limit, Sort.by("name")))
            .map(deckMapper::deckToDeckSimpleDto);
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{userId}/favorites/{deckId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Check if a deck is a favorite of the user")
    public void hasFavorite(@PathVariable Long userId, @PathVariable Long deckId) {
        LOGGER.info("GET /api/v1/users/{}/favorites/{}", userId, deckId);
        if (!favoriteService.hasFavorite(userId, deckId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s has no favorite %s", userId, deckId));
        }
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/{userId}/favorites/{deckId}")
    @ApiOperation(value = "Remove a deck from favorites of user")
    public void removeFavorite(@PathVariable Long userId, @PathVariable Long deckId) {
        LOGGER.info("GET /api/v1/users/{}/favorites/{}", userId, deckId);
        favoriteService.removeFavorite(userId, deckId);
    }

    @Secured("ROLE_USER")
    @PatchMapping(value = "/{id}")
    @ApiOperation(value = "Change settings of logged in user")
    public UserDetailsDto editSettings(@PathVariable long id, @Valid @RequestBody UserEditInquiryDto userEditInquiryDto) {
        return userMapper.userToUserDetailsDto(userService.editSettings(id, userMapper.userEditInquiryDtoToUser(userEditInquiryDto)));
    }
}
