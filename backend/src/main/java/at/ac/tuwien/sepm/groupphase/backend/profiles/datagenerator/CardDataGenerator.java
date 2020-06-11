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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;

@Profile("generateData")
@Component
public class CardDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int SIZE_OF_LARGE_TEST_DECK = 1000;
    private static final int SIZE_OF_SUPER_LARGE_TEST_DECK = 5000;
    private static final int NUMBER_OF_REVISIONS = 5;

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

    @PostConstruct
    private void generateLargeTestDeck() {
        User user = new User();
        user.setAuthId("real id");
        user.setDescription("test user3");
        user.setAdmin(false);
        user.setEnabled(false);
        user.setUsername("NutCase");
        userRepository.saveAndFlush(user);

        Deck deck = new Deck();
        deck.setName("large test deck");
        deck.setCreatedBy(user);
        deckRepository.saveAndFlush(deck);

        LOGGER.info("Creating {}({} cards)", deck.getName(), SIZE_OF_LARGE_TEST_DECK);
        for (int i = 0; i < SIZE_OF_LARGE_TEST_DECK; i++) {
            generateCard(deck, user, i, "large");
        }
    }

    @PostConstruct
    private void generateSuperLargeTestDeck() {
        User user = new User();
        user.setAuthId("unreal id");
        user.setDescription("test user4");
        user.setAdmin(false);
        user.setEnabled(false);
        user.setUsername("CamelCase");
        userRepository.saveAndFlush(user);

        Deck deck = new Deck();
        deck.setName("super large test deck");
        deck.setCreatedBy(user);
        deckRepository.saveAndFlush(deck);

        LOGGER.info("Creating {}({} cards)", deck.getName(), SIZE_OF_SUPER_LARGE_TEST_DECK);
        for (int i = 0; i < SIZE_OF_SUPER_LARGE_TEST_DECK; i++) {
            generateCard(deck, user, i, "super large");
        }
    }

    private void generateCard(Deck deck, User user, int index, String deckSize) {
        Card card = new Card();
        card.setDeck(deck);
        deck.getCards().add(card);
        Revision revision = new Revision();

        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setMessage(deckSize + " test set revision - card " + index);
        revision.setCreatedBy(user);
        user.getRevisions().add(revision);

        card = cardRepository.save(card);

        // Add content
        RevisionEdit edit = new RevisionEdit();
        edit.setTextFront(deckSize + " test set card " + index + " front");
        edit.setTextBack(deckSize + " test set card " + index + " back");
        revision.setRevisionEdit(edit);
        edit.setRevision(revision);

        card = cardRepository.save(card);
    }

}
