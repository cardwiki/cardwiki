package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RevisionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.Set;

@Service
public class SimpleCardService implements CardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final DeckService deckService;
    private final CardRepository cardRepository;
    private final RevisionRepository revisionRepository;

    public SimpleCardService(CardRepository cardRepository, RevisionRepository revisionRepository, DeckService deckService, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.deckService = deckService;
        this.revisionRepository = revisionRepository;
    }

    @Override
    @Transactional
    public Card addCardToDeck(Long deckId, RevisionEdit revisionEdit, String oAuthId) {
        LOGGER.debug("Add Card to Deck: {} {} {}", revisionEdit, deckId, oAuthId);
        User user = userService.loadUserByOauthId(oAuthId);
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

        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public Card findOne(Long deckId, Long cardId) {
        LOGGER.debug("Find card with id {} in deck with id {}", deckId, cardId);
        Deck deck = deckService.findOne(deckId);
        Optional<Card> card = cardRepository.findById(cardId);

        if (card.isPresent() && card.get().getDeck().getId().equals(deck.getId())) {
            return card.get();
        }
        else throw new NotFoundException(String.format("Could not find card with id %s in deck with id %s", cardId, deckId));
    }

    @Override
    public Card editCardInDeck(Long deckId, Long cardId, RevisionEdit revisionEdit, String oAuthId) {
        LOGGER.debug("Edit Card {} in Deck {}: {}", cardId, deckId, revisionEdit);
        User user = userService.loadUserByOauthId(oAuthId);
        Card card = findOne(deckId, cardId);
        Deck deck = deckService.findOne(deckId);
        card.setDeck(deck);

        Revision revision = new Revision();
        revision.setMessage("Edited");
        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setCreatedBy(user);

        // Add content
        card.getLatestRevision().setRevisionEdit(revisionEdit);
        revisionEdit.setRevision(card.getLatestRevision());

        return cardRepository.saveAndFlush(card);
    }

}
