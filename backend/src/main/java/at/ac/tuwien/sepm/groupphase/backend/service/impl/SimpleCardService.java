package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class SimpleCardService implements CardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final DeckService deckService;
    private final CardRepository cardRepository;

    public SimpleCardService(CardRepository cardRepository, DeckService deckService, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.deckService = deckService;
    }

    @Override
    @Transactional
    public Card addCardToDeck(Long deckId, RevisionCreate revisionCreate) {
        LOGGER.debug("Add Card to Deck: {} {}", revisionCreate, deckId);
        User user = userService.loadCurrentUser();
        Deck deck = deckService.findOne(deckId);

        // Save Card with initial revision
        Card card = new Card();
        card.setDeck(deck);

        revisionCreate.setMessage(revisionCreate.getMessage() != null ? revisionCreate.getMessage() : "Created");
        card.setLatestRevision(revisionCreate);
        revisionCreate.setCard(card);
        revisionCreate.setCreatedBy(user);

        return cardRepository.saveAndFlush(card);
    }

    @Override
    public Stream<RevisionEdit> findLatestEditRevisionsByDeckId(Long deckId) {
        LOGGER.debug("Find latest edit revisions id {}", deckId);
        return cardRepository.findLatestEditRevisionsByDeck_Id(deckId);
    }

    @Override
    @Transactional
    public void addDeleteRevisionToCard(Long cardId, String revisionMessage) {
        LOGGER.debug("Add delete-revision to card with id {}", cardId);
        Card card = findOne(cardId);
        User user = userService.loadCurrentUser();

        RevisionDelete revision = new RevisionDelete();
        revision.setCard(card);
        revision.setMessage(revisionMessage == null ? "Deleted" : revisionMessage);
        card.setLatestRevision(revision);
        revision.setCreatedBy(user);

        cardRepository.save(card);
    }

    @Transactional
    public Card findOne(Long cardId) {
        LOGGER.debug("Find card with id {}", cardId);
        Optional<Card> card = cardRepository.findSimpleById(cardId);
        return card.orElseThrow(() -> new NotFoundException("Could not find card with id " + cardId));
    }

    @Override
    @Transactional
    public Card editCardInDeck(Long cardId, RevisionEdit revisionEdit) {
        LOGGER.debug("Edit Card {}: {}", cardId, revisionEdit);
        User user = userService.loadCurrentUser();
        Card card = findOne(cardId);

        revisionEdit.setMessage(revisionEdit.getMessage() != null ? revisionEdit.getMessage() : "Edited");
        card.setLatestRevision(revisionEdit);
        revisionEdit.setCard(card);
        revisionEdit.setCreatedBy(user);

        return cardRepository.saveAndFlush(card);
    }
}
