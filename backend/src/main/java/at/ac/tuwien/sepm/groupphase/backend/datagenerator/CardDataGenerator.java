package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class CardDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_CARDS_TO_GENERATE = 3;

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    public CardDataGenerator(CardRepository cardRepository, DeckRepository deckRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void generateCards() {
        User user = new User();
        user.setAuthId("fake id");
        user.setDescription("test user");
        user.setAdmin(false);
        user.setEnabled(false);
        userRepository.saveAndFlush(user);
        Deck deck = new Deck();
        deck.setName("Test Deck");
        deck.setCreatedBy(user);
        deckRepository.saveAndFlush(deck);

        long countCards = cardRepository.count();
        for (int i = (int)countCards; i < NUMBER_OF_CARDS_TO_GENERATE; i++) {
            LOGGER.info("Creating card {}", i);
            Card card = new Card();
            Revision revision = new Revision();

            card.setDeck(deck);
            deck.getCards().add(card);

            card.setLatestRevision(revision);
            revision.setCard(card);
            revision.setMessage("Test Revision " + i);
            revision.setCreatedBy(user);
            user.getRevisions().add(revision);

            card = cardRepository.save(card);

            // Add content
            RevisionEdit edit = new RevisionEdit();
            edit.setTextFront("Front Text " + i);
            edit.setTextBack("Back Text " + i);

            revision.setRevisionEdit(edit);
            edit.setRevision(revision);

            card = cardRepository.save(card);

            if (i % 2 == 1) {
                // Add Delete-Revision
                revision = new Revision();
                revision.setCard(card);
                revision.setMessage("Deleted " + i);
                card.setLatestRevision(revision);
                revision.setCreatedBy(user);
                user.getRevisions().add(revision);

                cardRepository.save(card);
            }
        }
    }

}
