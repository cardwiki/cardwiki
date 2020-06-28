package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DeckMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RevisionMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "api/v1/decks")
public class DeckEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DeckService deckService;
    private final DeckMapper deckMapper;
    private final RevisionMapper revisionMapper;

    @Autowired
    public DeckEndpoint(DeckService deckService, DeckMapper deckMapper, RevisionMapper revisionMapper) {
        this.deckService = deckService;
        this.deckMapper = deckMapper;
        this.revisionMapper = revisionMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new card deck", authorizations = @Authorization("user"))
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
    @ApiOperation(value = "Update deck", authorizations = @Authorization("user"))
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
    @ApiOperation(value = "Copy a deck", authorizations = @Authorization("user"))
    public DeckDto copy(@PathVariable Long id, @Valid @RequestBody DeckInputDto deckInputDto) {
        LOGGER.info("Post /api/v1/decks/{}/copy body={}", id, deckInputDto);
        return deckMapper.deckToDeckDto(deckService.copy(id, deckMapper.deckInputDtoToDeck(deckInputDto)));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "delete deck", authorizations = @Authorization("admin"))
    public void deleteDeck(@PathVariable Long id) {
        LOGGER.info("DELETE /api/v1/decks/{}", id);
        deckService.delete(id);
	}

    @GetMapping(value = "/{id}/revisions")
    @ApiOperation(value = "Get revisions of the deck")
    public Page<RevisionDtoWithContent> getRevisions(@PathVariable Long id, @SortDefault(value = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        deckService.findOneOrThrow(id);
        return deckService.getRevisions(id, pageable).map(revision -> revisionMapper.revisionToRevisionDetailedDto(revision));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}/progress")
    @ApiOperation(value = "Get deck progress of current user", authorizations = @Authorization("user"))
    public DeckProgressDto getDeckProgress(@PathVariable Long id){
        return deckService.getProgress(id);
    }

    @GetMapping(value = "/{id}", produces = "text/csv")
    @ApiOperation(value = "export deck", authorizations = @Authorization("apiKey"))
    public void export(@PathVariable Long id, HttpServletResponse response) {
        LOGGER.info("GET /api/v1/decks/{} as .csv", id);
        deckService.findOneOrThrow(id);
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
