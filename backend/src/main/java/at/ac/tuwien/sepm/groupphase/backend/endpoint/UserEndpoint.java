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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
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
        LOGGER.info("POST /api/v1/users {} {}", token, userInputDto);
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
    public Page<UserDetailsDto> search(@Valid @NotNull @RequestParam String username, @SortDefault("username") Pageable pageable) {
        LOGGER.info("GET /api/v1/users?username={} {}", username, pageable);
        return userService.searchByUsername(username, pageable)
            .map(userMapper::userToUserDetailsDto);
    }

    @GetMapping(value = "/byname/{username}")
    @ApiOperation(value = "Get user profile")
    public UserDetailsDto getProfile(@PathVariable String username) {
        LOGGER.info("GET /api/v1/users/byname/{}", username);
        return userMapper.userToUserDetailsDto(userService.findUserByUsernameOrThrow(username));
    }

    @GetMapping(value = "/{id}/decks")
    @ApiOperation(value = "Get decks created by user")
    public Page<DeckSimpleDto> getDecks(@PathVariable long id, @SortDefault("name") Pageable pageable) {
        LOGGER.info("GET /api/v1/users/{}/decks {}", id, pageable);
        return userService.getDecks(id, pageable)
            .map(deckMapper::deckToDeckSimpleDto);
    }

    @GetMapping(value = "/{id}/revisions")
    @ApiOperation(value = "Get revisions created by user")
    public Page<RevisionDetailedDto> getRevisions(@PathVariable long id, @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        LOGGER.info("GET /api/v1/users/{}/revisions {}", id, pageable);
        return userService.getRevisions(id, pageable)
            .map(revision -> revisionMapper.revisionToRevisionDetailedDto(revision));
    }

    @Secured("ROLE_USER")
    @PutMapping(value = "/{userId}/favorites/{deckId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add deck to user favorites")
    public DeckSimpleDto addFavorite(@PathVariable Long userId, @PathVariable Long deckId) {
        LOGGER.info("PUT /api/v1/users/{}/favorites/{}", userId, deckId);
        return deckMapper.deckToDeckSimpleDto(favoriteService.addFavorite(userId, deckId));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{userId}/favorites")
    @ApiOperation(value = "Get favorites of user")
    public Page<DeckSimpleDto> getFavorites(@PathVariable Long userId, @SortDefault("name") Pageable pageable) {
        LOGGER.info("GET /api/v1/users/{}/favorites {}", userId, pageable);
        return favoriteService.getFavorites(userId, pageable)
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
        LOGGER.info("PATCH /api/v1/users/{} {}", id, userEditInquiryDto);
        return userMapper.userToUserDetailsDto(userService.editSettings(id, userMapper.userEditInquiryDtoToUser(userEditInquiryDto)));
    }
}
