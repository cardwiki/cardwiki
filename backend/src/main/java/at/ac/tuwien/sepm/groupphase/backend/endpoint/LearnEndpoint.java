package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CardMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/attempt")
    public void attempt(@Valid @RequestBody AttemptInputDto attempt){
        learnService.saveAttempt(attempt);
    }

    @GetMapping("/next")
    public CardDetailsDto getNextCard(@RequestParam Long deckId){
        return cardMapper.cardToCardDetailsDto(learnService.findNextCardByDeckId(deckId));
    }
}
