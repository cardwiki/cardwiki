package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DeckMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/v1/decks")
public class DeckEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckService deckService;
    private final DeckMapper deckMapper;

    @Autowired
    public DeckEndpoint(DeckService deckService, DeckMapper deckMapper) {
        this.deckService = deckService;
        this.deckMapper = deckMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new card deck", authorizations = @Authorization("ROLE_USER"))
    public DeckDto create(@Valid @RequestBody DeckInputDto deckInputDto) {
        LOGGER.info("POST /api/v1/decks body: {}", deckInputDto);
        return deckMapper.deckToDeckDto(
            deckService.create(
                deckMapper.deckInputDtoToDeck(deckInputDto)
            )
        );
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Search for card decks")
    public Page<DeckDto> search(@RequestParam String name, @SortDefault("name") Pageable pageable) {
        LOGGER.info("GET /api/v1/decks?name={} {}", name, pageable);
        return deckService.searchByName(name, pageable)
            .map(deckMapper::deckToDeckDto);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get a deck by id")
    public DeckDto findOne(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/decks/{}", id);
        try {
            return deckMapper.deckToDeckDto(deckService.findOneOrThrow(id));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_USER")
    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update deck", authorizations = @Authorization("ROLE_USER"))
    public DeckDto updateDeck(@PathVariable Long id, @Valid @RequestBody DeckUpdateDto deckUpdateDto) {
        LOGGER.info("PATCH /api/v1/decks/{} body: {}", id, deckUpdateDto);
        try {
            return deckMapper.deckToDeckDto(deckService.update(id, deckMapper.deckUpdateDtoToDeck(deckUpdateDto)));
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{id}/copy")
    @ApiOperation(value = "Copy a deck", authorizations = @Authorization("ROLE_USER"))
    public DeckDto copy(@PathVariable Long id, @Valid @RequestBody DeckInputDto deckInputDto) {
        LOGGER.info("Post /api/v1/decks/{}/copy body={}", id, deckInputDto);
        return deckMapper.deckToDeckDto(deckService.copy(id, deckMapper.deckInputDtoToDeck(deckInputDto)));
    }
}
