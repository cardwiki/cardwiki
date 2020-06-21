package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AttemptInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Provides repositories to the TestData interface
 */
@Transactional
public abstract class TestDataGenerator {
    public final String UTF_16_SAMPLE_TEXT = "ユ简크로أفضل البحوثΣὲ γνДесแผ∮E⋅∞∑çéèñé";

    // These functions should be deterministic.

    @Autowired
    private EntityManager em;

    private static User defaultUser(String username){
        User user = new User();
        user.setUsername(username);
        user.setDescription("some description");
        user.setAuthId(username);
        user.setEnabled(true);
        user.setAdmin(false);
        user.setDeleted(false);
        return user;
    }

    public Agent transientAgent(){
        return new Agent(false, defaultUser("gustav"));
    }

    public Agent transientAgent(String name){
        return new Agent(false, defaultUser(name));
    }

    public Agent persistentAgent(){
        return new Agent(true, defaultUser("gustav"));
    }

    public Agent persistentAgent(String name){
        return new Agent(true, defaultUser(name));
    }

    public class Agent {
        private User user;
        private boolean persist;

        public User getUser(){
            return user;
        }

        public Agent persist(){
            return new Agent(true, user);
        }

        public Agent unpersist(){
            return new Agent(false, user);
        }

        public Agent(boolean persist, User user){
            this.persist = persist;
            this.user = user;
            if (persist) {
                em.persist(user);
                em.flush();
            }
        }

        private void beforeReturn(Object o){
            if (persist) {
                em.persist(o);
                em.flush();
            }
        }

        public Agent makeAdmin() {
            user.setAdmin(true);
            beforeReturn(user);
            return this;
        }

        public Deck createDeck(){
            Deck deck = new Deck();
            deck.setName("some deck");
            deck.setCreatedBy(user);
            deck.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
            deck.setUpdatedAt(LocalDateTime.of(2020, 2, 1, 1, 1));
            beforeReturn(deck);
            return deck;
        }

        public Card createCardIn(Deck deck, RevisionCreate revisionCreate){
            Card card = new Card();
            card.setDeck(deck);
            card.setLatestRevision(revisionCreate);
            revisionCreate.setCreatedBy(user);
            revisionCreate.setCard(card);
            card.getRevisions().add(revisionCreate);
            deck.getCards().add(card);
            card.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
            beforeReturn(card);
            return card;
        }

        public Card createCardIn(Deck deck){
            RevisionCreate revisionCreate = new RevisionCreate();
            revisionCreate.setTextFront("front text");
            revisionCreate.setTextBack("back text");
            revisionCreate.setMessage("test message");
            revisionCreate.setCreatedBy(user);
            user.getRevisions().add(revisionCreate);
            return createCardIn(deck, revisionCreate);
        }

        public Comment createCommentIn(Deck deck){
            return createCommentIn(deck, "What a beautiful deck");
        }

        public Comment createCommentIn(Deck deck, String message) {
            Comment comment = new Comment();
            comment.setMessage(message);
            comment.setDeck(deck);
            comment.setCreatedBy(user);
            deck.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
            deck.setUpdatedAt(LocalDateTime.of(2020, 2, 1, 1, 1));
            beforeReturn(comment);
            return comment;
        }

        public RevisionEdit editCard(Card card, RevisionEdit revisionEdit){
            card.setLatestRevision(revisionEdit);
            card.getRevisions().add(revisionEdit);
            revisionEdit.setCard(card);
            beforeReturn(revisionEdit);
            return revisionEdit;
        }

        public RevisionEdit editCard(Card card){
            RevisionEdit revisionEdit = new RevisionEdit();
            revisionEdit.setTextFront("front text");
            revisionEdit.setTextBack("back text");
            revisionEdit.setMessage("test message");
            revisionEdit.setCreatedBy(user);
            user.getRevisions().add(revisionEdit);
            return editCard(card, revisionEdit);
        }

        public Category createCategory(String name){
            Category category = new Category();
            category.setName(name);
            category.setCreatedAt(LocalDateTime.of(2020, 1, 1, 1, 1));
            category.setUpdatedAt(LocalDateTime.of(2020, 2, 1, 1, 1));
            beforeReturn(category);
            return category;
        }

        public void deleteCard(Card card, RevisionDelete revisionDelete){
            revisionDelete.setCreatedBy(user);
            user.getRevisions().add(revisionDelete);
            card.setLatestRevision(revisionDelete);
            revisionDelete.setCard(card);
            beforeReturn(revisionDelete);
        }

        public void deleteCard(Card card){
            RevisionDelete revisionDelete = new RevisionDelete();
            revisionDelete.setMessage("test message");
            deleteCard(card, revisionDelete);
        }

        public Deck addFavorite(Deck deck) {
            user.getFavorites().add(deck);
            deck.getFavoredBy().add(user);
            beforeReturn(deck);
            return deck;
        }
    }

    // Auth id to login as a persisted user
    public String givenUserAuthId() {
        return persistentAgent("normal-user").getUser().getAuthId();
    }

    // Auth id to login as a persisted admin
    public String givenAdminAuthId() {
        return persistentAgent("im-a-admin").makeAdmin().getUser().getAuthId();
    }

    public Matcher<String> validIsoDateTime() {
        return new TypeSafeMatcher<>() {
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

    // EVERYTHING BELOW IS DEPRECATED

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private RevisionRepository revisionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ImageRepository imageRepository;

    private long userCounter = 0;

    @Deprecated
    public User givenApplicationUser() {
        userCounter++;
        User user = new User();
        user.setId(userCounter);
        user.setUsername("username-" + userCounter);
        user.setDescription("some user");
        user.setAuthId("service:" + userCounter);
        user.setAdmin(false);
        user.setEnabled(true);
        user.setDeleted(false);
        return userRepository.saveAndFlush(user);
    }

    @Deprecated
    public Deck givenDeck() {
        User user = givenApplicationUser();
        Deck deck = new Deck();
        deck.setName("deck name");
        deck.setCreatedBy(user);
        return deckRepository.saveAndFlush(deck);
    }

    @Deprecated
    public Card givenCard() {
        Deck deck = givenDeck();
        Card card = new Card();
        card.setDeck(deck);
        deck.getCards().add(card);

        return cardRepository.saveAndFlush(card);
    }

    @Deprecated
    public RevisionCreate givenCreateRevision() {
        User user = givenApplicationUser();
        Card card = givenCard();
        RevisionCreate revision = new RevisionCreate();
        revision.setMessage("message");
        revision.setCard(card);
        revision.setTextFront("foo");
        revision.setTextBack("foo");
        card.setLatestRevision(revision);
        revision.setCreatedBy(user);
        user.getRevisions().add(revision);

        return revisionRepository.saveAndFlush(revision);
    }

    @Deprecated
    public RevisionEdit givenRevisionEdit() {
        RevisionEdit revisionEdit = new RevisionEdit();
        revisionEdit.setTextFront("front");
        revisionEdit.setTextBack("back");
        revisionEdit.setMessage("test message");
        revisionEdit.setImageFront(givenImage());
        revisionEdit.setImageBack(givenImage());
        revisionEdit.setCreatedBy(givenApplicationUser());

        Card card = givenCard();
        card.setLatestRevision(revisionEdit);
        revisionEdit.setCard(card);
        return revisionRepository.saveAndFlush(revisionEdit);
    }

    @Deprecated
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

    @Deprecated
    public Progress givenProgress() {
        User user = givenApplicationUser();
        Card card = givenCard();

        Progress.Id id = new Progress.Id(user, card);
        Progress progress = new Progress(id);
        progress.setDue(LocalDateTime.now());

        em.persist(progress);
        em.flush();

        return progress;
    }

    public Image givenImage() {
        User user = givenApplicationUser();
        Image image = new Image();
        image.setCreatedBy(user);
        image.setFilename("test.png");

        return imageRepository.saveAndFlush(image);
    }

    Long DECK_ID = 0L;
    Long CARD_ID = 1L;
    Long REVISION_ID = 2L;
    Long REVISION_EDIT_ID = 3L;
    Long CATEGORY_ID = 4L;
    Long CATEGORY_2_ID = 5L;
    LocalDateTime CREATED_AT = LocalDateTime.now();
    LocalDateTime UPDATED_AT = LocalDateTime.now();

    @Deprecated
    public User getUnconnectedSampleUser() {
        userCounter++;
        User user = new User();
        user.setId(userCounter);
        user.setAuthId("service:" + userCounter);
        user.setUsername("username" + userCounter);
        user.setAdmin(false);
        user.setEnabled(true);
        user.setDeleted(false);
        user.setDescription("some user");
        return user;
    }

    @Deprecated
    public Deck getUnconnectedSampleDeck() {
        Deck deck = new Deck();
        deck.setId(DECK_ID);
        deck.setName("deck name");
        deck.setCreatedAt(CREATED_AT);
        deck.setUpdatedAt(UPDATED_AT);
        return deck;
    }

    @Deprecated
    public Card getUnconnectedSampleCard() {
        Card card = new Card();
        card.setId(CARD_ID);
        card.setCreatedAt(CREATED_AT);
        return card;
    }

    @Deprecated
    public RevisionCreate getUnconnectedSampleRevisionCreate() {
        RevisionCreate revision = new RevisionCreate();
        revision.setId(REVISION_ID);
        revision.setMessage("message");
        revision.setCreatedAt(CREATED_AT);
        return revision;
    }

    @Deprecated
    public RevisionEdit getUnconnectedSampleRevisionEdit() {
        RevisionEdit revisionEdit = new RevisionEdit();
        revisionEdit.setId(REVISION_EDIT_ID);
        revisionEdit.setTextFront("front");
        revisionEdit.setTextBack("back");
        return revisionEdit;
    }

    @Deprecated
    public Category getUnconnectedSampleCategory() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName("category name");
        category.setCreatedAt(CREATED_AT);
        category.setUpdatedAt(UPDATED_AT);
        return category;
    }

    @Deprecated
    public Category getUnconnectedSampleCategory2() {
        Category parent = new Category();
        parent.setId(CATEGORY_2_ID);
        parent.setName("Parent category name");
        parent.setCreatedAt(CREATED_AT);
        parent.setUpdatedAt(UPDATED_AT);
        return parent;
    }

    @Deprecated
    public User getSampleUser() {
        return getUnconnectedSampleUser();
    }

    @Deprecated
    public Deck getSampleDeck() {
        Deck deck = getUnconnectedSampleDeck();
        deck.setCreatedBy(getSampleUser());
        return deck;
    }

    @Deprecated
    public Card getSampleCard() {
        Card card = getUnconnectedSampleCard();
        card.setDeck(getSampleDeck());
        card.getDeck().getCards().add(card);
        return card;
    }

    @Deprecated
    public Revision getSampleRevision() {
        Revision revision = getUnconnectedSampleRevisionCreate();
        revision.setCreatedBy(getSampleUser());
        revision.getCreatedBy().getRevisions().add(revision);
        revision.setCard(getSampleCard());
        revision.getCard().getRevisions().add(revision);
        revision.getCard().setLatestRevision(revision);
        return revision;
    }

    @Deprecated
    public Category getSampleCategoryWithoutParent() {
        Category category = getUnconnectedSampleCategory();
        category.setCreatedBy(getSampleUser());
        category.setParent(null);
        return category;
    }

    @Deprecated
    public Category getSampleCategoryWithParent() {
        Category category = getSampleCategoryWithoutParent();
        Category parent = getUnconnectedSampleCategory2();
        parent.setCreatedBy(getSampleUser());
        category.setParent(parent);
        parent.getChildren().add(category);
        return category;
    }

    @Deprecated
    public AttemptInputDto getSampleAttempt() {
        Card card = getSampleCard();
        AttemptInputDto attempt = new AttemptInputDto();
        attempt.setCardId(card.getId());
        attempt.setStatus(AttemptInputDto.Status.GOOD);

        return attempt;
    }

    @Deprecated
    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public ImageRepository getImageRepository() {
        return imageRepository;
    }
}
