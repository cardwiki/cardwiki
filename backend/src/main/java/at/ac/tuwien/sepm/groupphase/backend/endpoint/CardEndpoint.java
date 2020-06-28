package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RevisionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionCreate;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1")
@Validated
public class CardEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CardService cardService;
    private final RevisionMapper revisionMapper;

    @Autowired
    public CardEndpoint(CardService cardService, RevisionMapper revisionMapper) {
        this.cardService = cardService;
        this.revisionMapper = revisionMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/decks/{deckId}/cards")
    @ApiOperation(value = "Create a new card", authorizations = {@Authorization("user")})
    public CardSimpleDto create(@Valid  @RequestBody RevisionInputDto revisionInputDto, @PathVariable Long deckId) {
        LOGGER.info("POST /api/v1/decks/{}/cards body: {}", deckId, revisionInputDto);
        RevisionCreate revision = revisionMapper.revisionEditDtoToRevisionCreate(revisionInputDto);
        return revisionMapper.revisionEditToCardSimpleDto((RevisionEdit) cardService.addCardToDeck(deckId, revision).getLatestRevision());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/decks/{deckId}/cards")
    @ApiOperation(value = "Get all cards for a specific deck")
    public Page<CardContentDto> getCardsByDeckId(@PathVariable Long deckId, @SortDefault("createdAt") Pageable pageable) {
        LOGGER.info("GET /api/v1/decks/{}/cards {}", deckId, pageable);
        return cardService.findLatestEditRevisionsByDeckId(deckId, pageable)
            .map(revisionMapper::revisionEditToCardContentDto);
    }

    @GetMapping(value = "/cards/{cardId}")
    @ApiOperation(value = "Get information about a specific card in deck")
    public CardUpdateDto findOne(@PathVariable Long cardId) {
        LOGGER.info("GET /api/v1/cards/{}", cardId);
        return revisionMapper.revisionEditToCardUpdateDto((RevisionEdit) cardService.findOneOrThrow(cardId).getLatestRevision());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/cards/{cardId}")
    @ApiOperation(value = "Edit a specific card in a deck", authorizations = {@Authorization("user")})
    public CardSimpleDto edit(@Valid  @RequestBody RevisionInputDto revisionInputDto, @PathVariable Long cardId) {
        LOGGER.info("PATCH /api/v1/cards/{} body: {}", cardId, revisionInputDto);
        RevisionEdit revision = revisionMapper.revisionEditDtoToRevisionEdit(revisionInputDto);
        return revisionMapper.revisionEditToCardSimpleDto((RevisionEdit) cardService.editCardInDeck(cardId, revision).getLatestRevision());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/cards/{cardId}")
    @ApiOperation(value = "Removes card from deck", authorizations = {@Authorization(value = "user")})
    public void addDeleteRevisionToCard(@Valid @RequestBody(required = false) ReasonDto message, @PathVariable Long cardId) {
        LOGGER.info("POST /api/v1/cards/{} Body:", message);
        cardService.addDeleteRevisionToCard(cardId, message == null ? null : message.getMessage());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/cards/{cardId}")
    @ApiOperation(value = "Deletes a card", authorizations = {@Authorization("admin")})
    public void delete(@PathVariable Long cardId) {
        LOGGER.info("DELETE card {}", cardId);
        cardService.delete(cardId);
	}

    @GetMapping(value = "/cards/{id}/revisions")
    @ApiOperation(value = "Get revisions of the card")
    public Page<RevisionDtoWithContent> getRevisionsOfCard(@PathVariable long id, @SortDefault(value = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        cardService.findOneOrThrow(id);
        return cardService.getRevisionsOfCard(id, pageable).map(revision -> revisionMapper.revisionToRevisionDetailedDto(revision));
    }

    @GetMapping(value = "/revisions/byid")
    @ApiOperation(value = "Get multiple revisions by id")
    public Map<Long, RevisionDtoWithContent> getRevisionsByIds(@RequestParam(name = "id") Long[] ids) {
        return cardService.getRevisionsByIds(ids).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Revision::getId, revisionMapper::revisionToRevisionDetailedDto));
    }

    @GetMapping("/revisions")
    @ApiOperation(value = "Get recent revisions")
    public Page<RevisionDtoWithDeck> getLatestRevisions(@SortDefault(value = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return cardService.getRecentRevisions(pageable).map(revisionMapper::revision_to_revisionDtoWithDeck);
    }
}
