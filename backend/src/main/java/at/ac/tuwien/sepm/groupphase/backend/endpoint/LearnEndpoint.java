package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CardMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

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
    @ApiOperation(value = "Submit an attempt at recalling a card", authorizations = {@Authorization(value = "apiKey")})
    public void attempt(@Valid @RequestBody AttemptInputDto attempt){
        learnService.saveAttempt(attempt);
    }

    @Secured("ROLE_USER")
    @GetMapping("/next")
    @ApiOperation(value = "Get the next card of a deck to learn", authorizations = {@Authorization(value = "apiKey")})
    public CardDetailsDto getNextCard(@RequestParam Long deckId){
        return cardMapper.cardToCardDetailsDto(learnService.findNextCardByDeckId(deckId));
    }
}
