package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardSimpleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CardMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
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
@RequestMapping(value = "/api/v1/learn")
public class LearnEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LearnService learnService;
    private final CardMapper cardMapper;

    @Autowired
    public LearnEndpoint(LearnService learnService, CardMapper cardMapper) {
        this.learnService = learnService;
        this.cardMapper = cardMapper;
    }

    @Secured("ROLE_USER")
    @PostMapping("/attempt")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Submit an attempt at recalling a card", authorizations = {@Authorization(value = "apiKey")})
    public void attempt(@Valid @RequestBody AttemptInputDto attempt){
        LOGGER.info("POST /api/v1/learn body: {}", attempt);
        learnService.saveAttempt(attempt);
    }

    @Secured("ROLE_USER")
    @GetMapping("/next")
    @ApiOperation(value = "Get the next cards of a deck to learn", authorizations = {@Authorization(value = "apiKey")})
    public List<CardSimpleDto> getNextCards(@RequestParam Long deckId, @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset){
        LOGGER.info("GET /api/v1/learn/next?deckId={}&limit={}&offset={}", deckId, limit, offset);
        return learnService.findNextCardsByDeckId(deckId, PageRequest.of(offset, limit)).stream()
            .map(cardMapper::cardToCardSimpleDto)
            .collect(Collectors.toList());
    }
}
