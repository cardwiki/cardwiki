package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CardService;
import at.ac.tuwien.sepm.groupphase.backend.service.DeckService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;

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

}
