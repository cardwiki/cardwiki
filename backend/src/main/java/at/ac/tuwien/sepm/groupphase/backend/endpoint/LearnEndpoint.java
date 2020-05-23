package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CardDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CardMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.LearnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/learn")
public class LearnEndpoint {
    @Autowired
    private LearnService learnService;

    @Autowired
    private CardMapper cardMapper;

    @PostMapping("/attempt")
    public void attempt(@Valid @RequestBody AttemptInputDto attempt){
        learnService.saveAttempt(attempt);
    }

    @PostMapping("/next")
    public CardDetailsDto getNextCard(@RequestParam Long deckId){
        return cardMapper.cardToCardDetailsDto(learnService.findNextCardByDeckId(deckId));
    }
}
