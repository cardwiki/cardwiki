package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DeckInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.DeckMapper;
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

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/decks")
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
}
