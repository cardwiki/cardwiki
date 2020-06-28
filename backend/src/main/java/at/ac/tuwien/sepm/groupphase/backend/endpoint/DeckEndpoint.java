package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DeckMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Deck;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
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
    public List<DeckDto> search(@RequestParam String name, @RequestParam Integer limit, @RequestParam Integer offset) {
        LOGGER.info("GET /api/v1/decks?name={}&limit={}&offset={}", name, limit, offset);
        return deckService.searchByName(name, PageRequest.of(offset, limit))
            .stream()
            .map(deckMapper::deckToDeckDto)
            .collect(Collectors.toList());
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

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "delete deck", authorizations = @Authorization("apiKey"))
    public void deleteDeck(@PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/decks/{}", id);
        deckService.delete(id);
    }

    @GetMapping(value = "/{id}", produces = "text/csv")
    @ApiOperation(value = "export deck", authorizations = @Authorization("apiKey"))
    public void export(@PathVariable Long id, HttpServletResponse response) {
        LOGGER.info("GET /api/v1/decks/{} as .csv", id);
        Deck deck = deckService.findOneOrThrow(id);
        response.addHeader("Content-Disposition", "attachment");
        response.addHeader("Content-Type", "text/csv;charset=UTF-8");
        try {
            deckService.createCsvData(response.getWriter(), id);
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error encoding cards.");
        }
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{id}/cards", consumes = "multipart/form-data")
    @ApiOperation(value = "import cards to deck", authorizations = @Authorization("apiKey"))
    public DeckDto importCards (@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        LOGGER.info("POST {} to /api/v1/decks/{}", file.getOriginalFilename(), id);
        try {
            return deckMapper.deckToDeckDto(deckService.addCards(id, file));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading cards.");
        }
    }
}
