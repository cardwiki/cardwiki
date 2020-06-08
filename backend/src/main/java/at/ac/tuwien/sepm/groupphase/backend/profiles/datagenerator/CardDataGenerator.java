package at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Scanner;

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
        generateDeck(0, "Hauptstädte Europas", "capitals.csv");
        generateDeck(1, "Mathematik Demo", "math.csv");
    }

    private void generateDeck(int id, String name, String filename) {
        User user = new User();
        user.setAuthId("fake id" + id);
        user.setDescription("test user");
        user.setAdmin(false);
        user.setEnabled(false);
        user.setUsername("crashtestdummy" + id);
        userRepository.saveAndFlush(user);
        Deck deck = new Deck();
        deck.setName(name);
        deck.setCreatedBy(user);
        deckRepository.saveAndFlush(deck);

        long countCards = cardRepository.count();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(filename);
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()){
            String[] parts = scanner.nextLine().split(",", 2);
            Card card = new Card();
            Revision revision = new Revision();

            card.setDeck(deck);
            deck.getCards().add(card);

            card.setLatestRevision(revision);
            revision.setCard(card);
            revision.setMessage("Test Revision ");
            revision.setCreatedBy(user);
            user.getRevisions().add(revision);

            card = cardRepository.save(card);

            // Add content
            RevisionEdit edit = new RevisionEdit();
            edit.setTextFront(parts[0]);
            edit.setTextBack(parts[1]);

            revision.setRevisionEdit(edit);
            edit.setRevision(revision);

            card = cardRepository.save(card);
        }
    }

}
