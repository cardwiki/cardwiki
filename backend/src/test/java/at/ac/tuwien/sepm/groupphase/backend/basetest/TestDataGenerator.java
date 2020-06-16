package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Provides repositories to the TestData interface
 */
@Transactional
public abstract class TestDataGenerator {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private RevisionRepository revisionRepository;
    @Autowired
    private RevisionEditRepository revisionEditRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProgressRepository progressRepository;

    private static long userCounter = 0;

    public User givenApplicationUser() {
        userCounter++;
        User user = new User();
        user.setId(userCounter);
        user.setUsername("username-" + userCounter);
        user.setDescription("some user");
        user.setAuthId("service:" + userCounter);
        user.setAdmin(false);
        user.setEnabled(true);
        return userRepository.saveAndFlush(user);
    }

    public Deck givenDeck() {
        User user = givenApplicationUser();
        Deck deck = new Deck();
        deck.setName("deck name");
        deck.setCreatedBy(user);
        return deckRepository.saveAndFlush(deck);
    }

    public Card givenCard() {
        Deck deck = givenDeck();
        Card card = new Card();
        card.setDeck(deck);
        deck.getCards().add(card);

        return cardRepository.saveAndFlush(card);
    }

    public Revision givenRevision() {
        User user = givenApplicationUser();
        Card card = givenCard();
        Revision revision = new Revision();
        revision.setMessage("message");
        revision.setCard(card);
        card.setLatestRevision(revision);
        revision.setCreatedBy(user);
        user.getRevisions().add(revision);

        return revisionRepository.saveAndFlush(revision);
    }

    public RevisionEdit givenRevisionEdit() {
        Revision revision = givenRevision();
        RevisionEdit revisionEdit = new RevisionEdit();
        revisionEdit.setTextFront("front");
        revisionEdit.setTextBack("back");
        revisionEdit.setRevision(revision);
        revision.setRevisionEdit(revisionEdit);

        return revisionEditRepository.saveAndFlush(revisionEdit);
    }

    public Category givenCategory() {
        Category category = new Category();
        Category parent = new Category();
        User user = givenApplicationUser();
        category.setCreatedBy(user);
        category.setName("category");
        parent.setName("parent category");

        parent = categoryRepository.saveAndFlush(parent);
        category.setParent(parent);
        return categoryRepository.saveAndFlush(category);
    }

    public Progress givenProgress() {
        User user = givenApplicationUser();
        Card card = givenCard();

        Progress.Id id = new Progress.Id(user, card);
        Progress progress = new Progress(id);
        progress.setDue(LocalDateTime.now());

        return progressRepository.saveAndFlush(progress);
    }

    Long DECK_ID = 0L;
    Long CARD_ID = 1L;
    Long REVISION_ID = 2L;
    Long REVISION_EDIT_ID = 3L;
    Long CATEGORY_ID = 4L;
    Long CATEGORY_2_ID = 5L;
    LocalDateTime CREATED_AT = LocalDateTime.now();
    LocalDateTime UPDATED_AT = LocalDateTime.now();

    public User getUnconnectedSampleUser() {
        userCounter++;
        User user = new User();
        user.setId(userCounter);
        user.setAuthId("service:" + userCounter);
        user.setUsername("username" + userCounter);
        user.setAdmin(false);
        user.setEnabled(true);
        user.setDescription("some user");
        return user;
    }

    public Deck getUnconnectedSampleDeck() {
        Deck deck = new Deck();
        deck.setId(DECK_ID);
        deck.setName("deck name");
        deck.setCreatedAt(CREATED_AT);
        deck.setUpdatedAt(UPDATED_AT);
        return deck;
    }

    public Card getUnconnectedSampleCard() {
        Card card = new Card();
        card.setId(CARD_ID);
        card.setCreatedAt(CREATED_AT);
        return card;
    }

    public Revision getUnconnectedSampleRevision() {
        Revision revision = new Revision();
        revision.setId(REVISION_ID);
        revision.setMessage("message");
        revision.setCreatedAt(CREATED_AT);
        return revision;
    }

    public RevisionEdit getUnconnectedSampleRevisionEdit() {
        RevisionEdit revisionEdit = new RevisionEdit();
        revisionEdit.setId(REVISION_EDIT_ID);
        revisionEdit.setTextFront("front");
        revisionEdit.setTextBack("back");
        return revisionEdit;
    }

    public Category getUnconnectedSampleCategory() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName("category name");
        category.setCreatedAt(CREATED_AT);
        category.setUpdatedAt(UPDATED_AT);
        return category;
    }

    public Category getUnconnectedSampleCategory2() {
        Category parent = new Category();
        parent.setId(CATEGORY_2_ID);
        parent.setName("Parent category name");
        parent.setCreatedAt(CREATED_AT);
        parent.setUpdatedAt(UPDATED_AT);
        return parent;
    }

    public User getSampleUser() {
        return getUnconnectedSampleUser();
    }

    public Deck getSampleDeck() {
        Deck deck = getUnconnectedSampleDeck();
        deck.setCreatedBy(getSampleUser());
        return deck;
    }

    public Card getSampleCard() {
        Card card = getUnconnectedSampleCard();
        card.setDeck(getSampleDeck());
        card.getDeck().getCards().add(card);
        return card;
    }

    public Revision getSampleRevision() {
        Revision revision = getUnconnectedSampleRevision();
        revision.setCreatedBy(getSampleUser());
        revision.getCreatedBy().getRevisions().add(revision);
        revision.setCard(getSampleCard());
        revision.getCard().getRevisions().add(revision);
        revision.getCard().setLatestRevision(revision);
        return revision;
    }

    public RevisionEdit getSampleRevisionEdit() {
        RevisionEdit revisionEdit = getUnconnectedSampleRevisionEdit();
        revisionEdit.setRevision(getSampleRevision());
        revisionEdit.getRevision().setRevisionEdit(revisionEdit);
        return revisionEdit;
    }

    public Category getSampleCategoryWithoutParent() {
        Category category = getUnconnectedSampleCategory();
        category.setCreatedBy(getSampleUser());
        category.setParent(null);
        return category;
    }

    public Category getSampleCategoryWithParent() {
        Category category = getSampleCategoryWithoutParent();
        Category parent = getUnconnectedSampleCategory2();
        parent.setCreatedBy(getSampleUser());
        category.setParent(parent);
        parent.getChildren().add(category);
        return category;
    }

    public Matcher<String> validIsoDateTime() {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String s) {
                try {
                    DateTimeFormatter.ISO_DATE_TIME.parse(s);
                    return true;
                } catch (DateTimeParseException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("string matches ISO datetime format");
            }
        };
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public DeckRepository getDeckRepository() {
        return deckRepository;
    }

    public CardRepository getCardRepository() {
        return cardRepository;
    }

    public RevisionRepository getRevisionRepository() {
        return revisionRepository;
    }

    public RevisionEditRepository getRevisionEditRepository() {
        return revisionEditRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }
}
