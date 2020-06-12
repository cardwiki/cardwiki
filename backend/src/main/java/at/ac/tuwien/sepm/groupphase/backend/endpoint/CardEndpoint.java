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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/decks/{deckId}/cards")
public class CardEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CardService cardService;
    private final CardMapper cardMapper;
    private final Path path;

    @Autowired
    public CardEndpoint(CardService cardService, CardMapper cardMapper, @Value("${cawi.image-served-path}") String path) {
        this.cardService = cardService;
        this.cardMapper = cardMapper;
        this.path = Paths.get(path);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @ApiOperation(value = "Create a new card", authorizations = {@Authorization(value = "ROLE_USER")})
    public CardDetailsDto create(@Valid  @RequestBody RevisionEditInquiryDto revisionEditInquiryDto, @PathVariable Long deckId) {
        LOGGER.info("POST /api/v1/decks/{}/cards body: {}", deckId, revisionEditInquiryDto);
        RevisionEdit revisionEdit = cardMapper.revisionEditInquiryDtoToRevisionEdit(revisionEditInquiryDto);
        return cardMapper.cardToCardDetailsDto(cardService.addCardToDeck(deckId, revisionEdit));
    }

    @GetMapping(value = "/{cardId}")
    @ApiOperation(value = "Get information about a specific card in deck")
    public CardSimpleDto findOne(@PathVariable Long deckId, @PathVariable Long cardId) {
        LOGGER.info("GET /api/v1/decks/{}/cards/{}", deckId, cardId);
        CardSimpleDto cardSimpleDto = cardMapper.cardToCardSimpleDto(cardService.findOne(deckId, cardId));
        if (cardSimpleDto.getImageFront() != null) cardSimpleDto.setImageFront(path.resolve(cardSimpleDto.getImageFront()).toString());
        if (cardSimpleDto.getImageBack() != null) cardSimpleDto.setImageBack(path.resolve(cardSimpleDto.getImageBack()).toString());
        return cardSimpleDto;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/{cardId}")
    @ApiOperation(value = "Edit a specific card in a deck", authorizations = {@Authorization(value = "ROLE_USER")})
    public CardDetailsDto edit(@Valid  @RequestBody RevisionEditInquiryDto revisionEditInquiryDto, @PathVariable Long deckId, @PathVariable Long cardId) {
        LOGGER.info("PATCH /api/v1/decks/{}/cards/{} body: {}", deckId, cardId, revisionEditInquiryDto);
        RevisionEdit revisionEdit = cardMapper.revisionEditInquiryDtoToRevisionEdit(revisionEditInquiryDto);
        return cardMapper.cardToCardDetailsDto(cardService.editCardInDeck(deckId, cardId, revisionEdit));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @ApiOperation(value = "Get all cards for a specific deck")
    public List<CardContentDto> getCardsByDeckId(@PathVariable Long deckId) {
        LOGGER.info("GET /api/v1/decks/{}/cards", deckId);
        List<CardContentDto> cardContentDtos = cardMapper.cardToCardContentDto(cardService.findCardsByDeckId(deckId));
        for (CardContentDto cardContentDto : cardContentDtos) {
           if (cardContentDto.getImageFront() != null) cardContentDto.setImageFront(path.resolve(cardContentDto.getImageFront()).toString());
           if (cardContentDto.getImageBack() != null) cardContentDto.setImageBack(path.resolve(cardContentDto.getImageBack()).toString());
        }
        return cardContentDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{cardId}")
    @ApiOperation(value = "Removes card from deck", authorizations = {@Authorization(value = "ROLE_USER")})
    public CardContentDto addDeleteRevisionToCard(@PathVariable Long deckId, @PathVariable Long cardId) {
        LOGGER.info("DELETE /api/v1/decks/{}/cards/{}", deckId, cardId);
        return cardMapper.cardToCardContentDto(cardService.addDeleteRevisionToCard(deckId, cardId));
    }
}
