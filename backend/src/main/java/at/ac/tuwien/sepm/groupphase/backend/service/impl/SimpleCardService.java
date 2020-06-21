package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.CardNotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ProgressRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class SimpleCardService implements CardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final DeckService deckService;
    private final CardRepository cardRepository;
    private final ProgressRepository progressRepository;
    private final ImageService imageService;

    public SimpleCardService(
        CardRepository cardRepository,
        DeckService deckService,
        UserService userService,
        ProgressRepository progressRepository,
        ImageService imageService)
    {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.deckService = deckService;
        this.progressRepository = progressRepository;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public Card addCardToDeck(Long deckId, RevisionCreate revisionCreate) {
        LOGGER.debug("Add Card to Deck: {} {}", revisionCreate, deckId);
        User user = userService.loadCurrentUserOrThrow();
        Deck deck = deckService.findOneOrThrow(deckId);
        validateRevisionEdit(revisionCreate);

        // Save Card with initial revision
        Card card = new Card();
        card.setDeck(deck);

        revisionCreate.setMessage(revisionCreate.getMessage() != null ? revisionCreate.getMessage() : "Created");
        try {
            if (revisionCreate.getImageFront() != null)
                imageService.findOneOrThrow(revisionCreate.getImageFront().getFilename());
            if (revisionCreate.getImageBack() != null)
                imageService.findOneOrThrow(revisionCreate.getImageBack().getFilename());
        } catch (ImageNotFoundException ex) {
            throw new BadRequestException(ex.getMessage());
        }
        card.setLatestRevision(revisionCreate);
        revisionCreate.setCard(card);
        revisionCreate.setCreatedBy(user);

        return cardRepository.saveAndFlush(card);
    }

    @Override
    @Transactional
    public Page<RevisionEdit> findLatestEditRevisionsByDeckId(Long deckId, Pageable pageable) {
        LOGGER.debug("Find latest edit revisions id {} {}", deckId, pageable); // TODO
        findOneOrThrow(deckId);
        return cardRepository.findLatestEditRevisionsByDeck_Id(deckId, pageable);
    }

    @Override
    @Transactional
    public void addDeleteRevisionToCard(Long cardId, String revisionMessage) {
        LOGGER.debug("Add delete-revision to card with id {}", cardId);
        Card card = findOneOrThrow(cardId);
        User user = userService.loadCurrentUserOrThrow();

        RevisionDelete revision = new RevisionDelete();
        revision.setCard(card);
        revision.setMessage(revisionMessage == null ? "Deleted" : revisionMessage);
        card.setLatestRevision(revision);
        revision.setCreatedBy(user);

        cardRepository.save(card);
    }

    @Transactional
    public Card findOneOrThrow(Long cardId) {
        LOGGER.debug("Find card with id {}", cardId);
        Optional<Card> card = cardRepository.findById(cardId);
        return card.orElseThrow(() -> new CardNotFoundException(("Could not find card with id " + cardId)));
    }

    @Override
    @Transactional
    public void delete(Long cardId) {
        LOGGER.debug("Delete card with id {}", cardId);
        if (progressRepository.existsCardWithProgress(cardId)) {
            progressRepository.deleteCardProgress(cardId);
        }
        try {
            cardRepository.deleteById(cardId);
        } catch (EmptyResultDataAccessException e) {
            throw new CardNotFoundException("Could not find card with id " + cardId);
        }
    }

    @Override
    @Transactional
    public Card editCardInDeck(Long cardId, RevisionEdit revisionEdit) {
        LOGGER.debug("Edit Card {}: {}", cardId, revisionEdit);
        User user = userService.loadCurrentUserOrThrow();
        Card card = findOneOrThrow(cardId);
        validateRevisionEdit(revisionEdit);

        revisionEdit.setMessage(revisionEdit.getMessage() != null ? revisionEdit.getMessage() : "Edited");
        try {
            if (revisionEdit.getImageFront() != null)
                imageService.findOneOrThrow(revisionEdit.getImageFront().getFilename());
            if (revisionEdit.getImageBack() != null)
                imageService.findOneOrThrow(revisionEdit.getImageBack().getFilename());
        } catch (ImageNotFoundException ex) {
            throw new BadRequestException(ex.getMessage());
        }
        card.setLatestRevision(revisionEdit);
        revisionEdit.setCard(card);
        revisionEdit.setCreatedBy(user);

        return cardRepository.saveAndFlush(card);
    }

    public void validateRevisionEdit(RevisionEdit revisionEdit) {
        if (revisionEdit.getTextFront() == null && revisionEdit.getImageFront() == null) {
            throw new BadRequestException("Front side cannot be empty");
        }
        if (revisionEdit.getTextBack() == null && revisionEdit.getImageBack() == null) {
            throw new BadRequestException("Back side cannot be empty");
        }
    }

}
