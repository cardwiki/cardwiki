package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.ImageService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimpleCardService implements CardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final DeckService deckService;
    private final CardRepository cardRepository;
    private final ImageService imageService;

    public SimpleCardService(CardRepository cardRepository, DeckService deckService,
                             UserService userService, ImageService imageService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.deckService = deckService;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public Card addCardToDeck(Long deckId, RevisionEdit revisionEdit) {
        LOGGER.debug("Add Card to Deck: {} {}", revisionEdit, deckId);
        User user = userService.loadCurrentUser();
        Deck deck = deckService.findOne(deckId);

        // Save Card with initial revision
        Card card = new Card();
        card.setDeck(deck);

        Revision revision = new Revision();
        revision.setMessage("Created");
        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setCreatedBy(user);

        card = cardRepository.saveAndFlush(card);

        // Add content
        card.getLatestRevision().setRevisionEdit(revisionEdit);
        revisionEdit.setRevision(card.getLatestRevision());

        // Add revision edit to image(s)
        if (revisionEdit.getImageFront() != null) {
            imageService.findById(revisionEdit.getImageFront().getId()).getFrontSides().add(revisionEdit);
        }
        if (revisionEdit.getImageBack() != null) {
            imageService.findById(revisionEdit.getImageBack().getId()).getBackSides().add(revisionEdit);
        }

        return cardRepository.save(card);
    }

    @Override
    public List<Card> findCardsByDeckId(Long deckId) {
        LOGGER.debug("Find all cards for deck with id {}", deckId);
        return cardRepository.findCardsByDeck_Id(deckId).stream().filter(card -> card.getLatestRevision().getRevisionEdit() != null).collect(Collectors.toList()); //TODO do this in database query
    }

    @Override
    @Transactional
    public Card addDeleteRevisionToCard(Long deckId, Long cardId) {
        LOGGER.debug("Add delete-revision to card with id {} from deck with id {}", cardId, deckId);
        Card card = findOne(deckId, cardId);
        User user = userService.loadCurrentUser();

        Revision revision = new Revision();
        revision.setCard(card);
        revision.setMessage("Deleted");
        card.setLatestRevision(revision);
        revision.setCreatedBy(user);

        return cardRepository.save(card);
    }

    @Transactional
    public Card findOne(Long deckId, Long cardId) {
        LOGGER.debug("Find card with id {} in deck with id {}", deckId, cardId);
        Deck deck = deckService.findOne(deckId);
        Optional<Card> card = cardRepository.findSimpleById(cardId);

        if (card.isPresent() && card.get().getDeck().getId().equals(deck.getId())) {
            return card.get();
        }
        else throw new NotFoundException(String.format("Could not find card with id %s in deck with id %s", cardId, deckId));
    }

    @Override
    @Transactional
    public Card editCardInDeck(Long deckId, Long cardId, RevisionEdit revisionEdit) {
        LOGGER.debug("Edit Card {} in Deck {}: {}", cardId, deckId, revisionEdit);
        User user = userService.loadCurrentUser();
        Deck deck = deckService.findOne(deckId);
        Optional<Card> optCard = cardRepository.findDetailsById(cardId);

        if (optCard.isPresent() && optCard.get().getDeck().getId().equals(deck.getId())) {
            Card card = optCard.get();

            Revision revision = new Revision();
            revision.setMessage("Edited");
            card.setLatestRevision(revision);
            revision.setCard(card);
            revision.setCreatedBy(user);

            // Add content
            card.getLatestRevision().setRevisionEdit(revisionEdit);
            revisionEdit.setRevision(card.getLatestRevision());

            // Add revision edits to images
            if (revisionEdit.getImageFront() != null) {
                imageService.findById(revisionEdit.getImageFront().getId()).getFrontSides().add(revisionEdit);
            }
            if (revisionEdit.getImageBack() != null) {
                imageService.findById(revisionEdit.getImageBack().getId()).getBackSides().add(revisionEdit);
            }

            return cardRepository.saveAndFlush(card);
        }
        else throw new NotFoundException(String.format("Could not find card with id %s in deck with id %s", cardId, deckId));
    }

}
