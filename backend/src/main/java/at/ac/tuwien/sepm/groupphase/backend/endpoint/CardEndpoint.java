package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CardMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/decks/{deckId}/cards")
public class CardEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CardService cardService;
    private final CardMapper cardMapper;

    @Autowired
    public CardEndpoint(CardService cardService, CardMapper cardMapper) {
        this.cardService = cardService;
        this.cardMapper = cardMapper;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new card")
    public CardDetailsDto create(@Valid  @RequestBody RevisionEditInquiryDto revisionEditInquiryDto, Authentication authentication, @PathVariable Long deckId) {
        LOGGER.info("POST /api/v1/decks/{}/cards body: {}", deckId, revisionEditInquiryDto);
        RevisionEdit revisionEdit = cardMapper.revisionEditInquiryDtoToRevisionEdit(revisionEditInquiryDto);
        return cardMapper.cardToCardDetailsDto(cardService.addCardToDeck(deckId, revisionEdit, authentication.getName()));
    }

    @GetMapping(value = "/{cardId}")
    @ApiOperation(value = "Get information about a specific card in deck")
    public CardSimpleDto findOne(@PathVariable Long deckId, @PathVariable Long cardId) {
        LOGGER.info("GET /api/v1/decks/{}/cards/{}", deckId, cardId);
        return cardMapper.cardToCardSimpleDto(cardService.findOne(deckId, cardId));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{cardId}")
    @ApiOperation(value = "Edit a specific card in a deck")
    public CardDetailsDto edit(@Valid  @RequestBody RevisionEditInquiryDto revisionEditInquiryDto, Authentication authentication, @PathVariable Long deckId, @PathVariable Long cardId) {
        LOGGER.info("PATCH /api/v1/decks/{}/cards/{} body: {}", deckId, cardId, revisionEditInquiryDto);
        RevisionEdit revisionEdit = cardMapper.revisionEditInquiryDtoToRevisionEdit(revisionEditInquiryDto);
        return cardMapper.cardToCardDetailsDto(cardService.editCardInDeck(deckId, cardId, revisionEdit, authentication.getName()));
    }
}
