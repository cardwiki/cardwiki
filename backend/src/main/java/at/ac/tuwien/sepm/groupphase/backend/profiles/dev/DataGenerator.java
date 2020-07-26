package at.ac.tuwien.sepm.groupphase.backend.profiles.dev;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.profiles.datagenerator.Agent;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Profile("dev")
@Component
public class DataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int SIZE_OF_LARGE_TEST_DECK = 1000;
    private static final int SIZE_OF_SUPER_LARGE_TEST_DECK = 5000;
    private static final int NUMBER_OF_REVISIONS = 5;
    private static final int NUMBER_OF_CATEGORIES_TO_GENERATE = 3;

    private static final String[] USERNAMES = {DeveloperLogin.USER_NAME, "romeo", "julia", "victor", "mike", "juliett"};

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ApplicationContext appContext;

    @Value("${ci:#{null}}")
    private String ci;

    public DataGenerator(CardRepository cardRepository, DeckRepository deckRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.cardRepository = cardRepository;
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void setup(){
        if (userRepository.count() > 0){
            LOGGER.info("skipping data generation since database isn't empty, to regenerate the data delete the database");
            return;
        }

        List<Agent> agents = Arrays.stream(USERNAMES).map(username -> new Agent(em, true, Agent.defaultUser(username))).collect(Collectors.toList());

        User admin = Agent.defaultUser(DeveloperLogin.ADMIN_NAME);
        admin.setAdmin(true);
        new Agent(em, true, admin);

        Category science = agents.get(4).createCategory("Wissenschaften");
        Category geography = agents.get(2).addSubcategory(science, "Geographie");
        Category math = agents.get(3).addSubcategory(science, "Mathematik");

        Deck capitals = agents.get(0).createDeck();
        capitals.setName("Hauptstädte Europas");
        agents.get(0).addCategory(capitals, geography);
        importDeck(capitals, agents.get(0),"capitals.csv");

        Deck latex = agents.get(1).createDeck();
        latex.setName("Mathematik Demo");
        agents.get(1).addCategory(latex, math);
        importDeck(latex, agents.get(1),"math.csv");

        agents.get(1).createCommentIn(capitals, "Is there already a deck for capitals of the whole world?");
        agents.get(0).createCommentIn(capitals, "No, but feel free to start it by forking this deck!");
        agents.get(0).createCommentIn(latex, "We should add some examples for partial integration.");

        generateLargeTestDeck();
        generateSuperLargeTestDeck();
        generateJapaneseDeck();

        if (ci != null && ci.equals("true")){
            LOGGER.info("Detected CI mode ... shutting down");
            SpringApplication.exit(appContext, () -> 0);
        }
    }

    private void importDeck(Deck deck, Agent agent, String filename) {

        long countCards = cardRepository.count();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(filename);
        Scanner scanner = new Scanner(is);

        while (scanner.hasNextLine()){
            String[] parts = scanner.nextLine().split(",", 2);
            RevisionCreate create = new RevisionCreate();
            create.setMessage("import from CSV");
            create.setTextFront(parts[0]);
            create.setTextBack(parts[1]);
            agent.createCardIn(deck, create);
        }
    }

    private void generateLargeTestDeck() {
        User user = new User();
        user.setAuthId("real id");
        user.setDescription("test user3");
        user.setAdmin(false);
        user.setEnabled(false);
        user.setDeleted(false);
        user.setUsername("nununu");
        user.setTheme("LIGHT");
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

    private void generateSuperLargeTestDeck() {
        User user = new User();
        user.setAuthId("unreal id");
        user.setDescription("test user4");
        user.setAdmin(false);
        user.setEnabled(false);
        user.setDeleted(false);
        user.setUsername("nocamelcase");
        user.setTheme("LIGHT");
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
        RevisionCreate revision = new RevisionCreate();

        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setMessage(deckSize + " test set revision - card " + index);
        revision.setCreatedBy(user);
        user.getRevisions().add(revision);

        revision.setTextFront(deckSize + " test set card " + index + " front");
        revision.setTextBack(deckSize + " test set card " + index + " back");

        card = cardRepository.save(card);
    }

    private void generateJapaneseDeck() {
        User user = new User();
        user.setAuthId("fake");
        user.setDescription("");
        user.setAdmin(false);
        user.setEnabled(false);
        user.setDeleted(false);
        user.setUsername("test-user");

        userRepository.saveAndFlush(user);

        Deck deck = new Deck();
        deck.setName("第3課");
        deck.setCreatedBy(user);
        deckRepository.saveAndFlush(deck);

        final String[] fronts = {"飛行機", "電車", "家", "地下鉄", "休み", "銀行"};
        final String[] backs = {"plane", "train", "home", "subway", "vacation", "Bank"};

        for (int i = 0; i < Math.min(fronts.length, backs.length); i++) {
            insertNewCard(deck, user, fronts[i], backs[i]);
        }

    }

    private Card insertNewCard(Deck deck, User user, String front, String back) {
        Card card = new Card();
        card.setDeck(deck);
        deck.getCards().add(card);
        RevisionCreate revision = new RevisionCreate();

        card.setLatestRevision(revision);
        revision.setCard(card);
        revision.setMessage("created");
        revision.setCreatedBy(user);
        user.getRevisions().add(revision);

        revision.setTextFront(front);
        revision.setTextBack(back);

        return cardRepository.save(card);
    }
}
