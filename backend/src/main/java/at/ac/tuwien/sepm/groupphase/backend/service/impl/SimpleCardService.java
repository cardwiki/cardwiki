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
    public Card addCardToDeck(Long deckId, RevisionEdit revisionEdit, Authentication authentication) {
        LOGGER.debug("Add Card to Deck: {} {}", revisionEdit, deckId);
        User user = userService.loadUserByOauthId(authentication.getName());
        Deck deck = deckService.findOne(deckId);

        // Save Card with initial revision
        Card card = new Card();
        card.setDeck(deck);
        deck.getCards().add(card);

        Revision revision = new Revision();
        revision.setMessage("Created");
        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setCreatedBy(user);
        user.getRevisions().add(revision);

        card = cardRepository.saveAndFlush(card);

        // Add content
        card.getLatestRevision().setRevisionEdit(revisionEdit);
        revisionEdit.setRevision(card.getLatestRevision());

        return cardRepository.save(card);
    }

}
