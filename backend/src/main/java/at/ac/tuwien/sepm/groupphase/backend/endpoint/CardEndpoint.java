package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CardMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
@Validated
public class CardEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CardService cardService;
    private final CardMapper cardMapper;

    @Autowired
    public CardEndpoint(CardService cardService, CardMapper cardMapper) {
        this.cardService = cardService;
        this.cardMapper = cardMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/decks/{deckId}/cards")
    @ApiOperation(value = "Create a new card", authorizations = {@Authorization(value = "ROLE_USER")})
    public CardDetailsDto create(@Valid  @RequestBody RevisionInputDto revisionInputDto, @PathVariable Long deckId) {
        LOGGER.info("POST /api/v1/decks/{}/cards body: {}", deckId, revisionInputDto);
        Revision revision = cardMapper.revisionInputDtoToRevision(revisionInputDto);
        return cardMapper.cardToCardDetailsDto(cardService.addCardToDeck(deckId, revision));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/decks/{deckId}/cards")
    @ApiOperation(value = "Get all cards for a specific deck")
    public List<CardContentDto> getCardsByDeckId(@PathVariable Long deckId) {
        LOGGER.info("GET /api/v1/decks/{}/cards", deckId);
        return cardMapper.cardToCardContentDto(cardService.findCardsByDeckId(deckId));
    }

    @GetMapping(value = "/cards/{cardId}")
    @ApiOperation(value = "Get information about a specific card in deck")
    public CardSimpleDto findOne(@PathVariable Long cardId) {
        LOGGER.info("GET /api/v1/cards/{}", cardId);
        return cardMapper.cardToCardSimpleDto(cardService.findOne(cardId));
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/cards/{cardId}")
    @ApiOperation(value = "Edit a specific card in a deck", authorizations = {@Authorization(value = "ROLE_USER")})
    public CardDetailsDto edit(@Valid  @RequestBody RevisionInputDto revisionInputDto, @PathVariable Long cardId) {
        LOGGER.info("PATCH /api/v1/cards/{} body: {}", cardId, revisionInputDto);
        Revision revision = cardMapper.revisionInputDtoToRevision(revisionInputDto);
        return cardMapper.cardToCardDetailsDto(cardService.editCardInDeck(cardId, revision));
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/cards/{cardId}")
    @ApiOperation(value = "Removes card from deck", authorizations = {@Authorization(value = "ROLE_USER")})
    public CardContentDto addDeleteRevisionToCard(@PathVariable Long cardId, @RequestParam(required = false) @Size(max = Revision.MAX_MESSAGE_SIZE) String message) {
        LOGGER.info("DELETE /api/v1/cards/{}?message=", message);
        return cardMapper.cardToCardContentDto(cardService.addDeleteRevisionToCard(cardId, message));
    }
}
