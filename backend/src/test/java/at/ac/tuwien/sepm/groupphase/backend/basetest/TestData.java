package at.ac.tuwien.sepm.groupphase.backend.basetest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public interface TestData {

    UserRepository getUserRepository();
    DeckRepository getDeckRepository();
    CardRepository getCardRepository();
    RevisionRepository getRevisionRepository();
    RevisionEditRepository getRevisionEditRepository();
    CategoryRepository getCategoryRepository();

    String OAUTH_ID = "Fake Id";
    String USER_NAME = "Test User";
    String DECK_NAME = "Test Deck";
    String REVISION_MESSAGE = "Test Revision";
    String FRONT_TEXT = "Test Front";
    String BACK_TEXT = "Test Back";
    String UTF_16_SAMPLE_TEXT = "ユ简크로أفضل البحوثΣὲ γνДесแผ∮E⋅∞∑çéèñé";
    String CATEGORY_NAME = "Test Category";
    String PARENT_CATEGORY_NAME = "Test Parent Category";

    default User givenApplicationUser() {
        User user = new User();
        user.setUsername(USER_NAME);
        user.setDescription("some user");
        user.setOAuthId(OAUTH_ID);
        return getUserRepository().saveAndFlush(user);
    }

    default Deck givenDeck() {
        User user = givenApplicationUser();
        Deck deck = new Deck();
        deck.setName(DECK_NAME);
        deck.setCreatedBy(user);
        return getDeckRepository().saveAndFlush(deck);
    }

    default Card givenCard() {
        Deck deck = givenDeck();
        Card card = new Card();
        card.setDeck(deck);
        deck.getCards().add(card);

        return getCardRepository().saveAndFlush(card);
    }

    default Revision givenRevision() {
        User user = givenApplicationUser();
        Card card = givenCard();
        Revision revision = new Revision();
        revision.setMessage(REVISION_MESSAGE);
        revision.setCard(card);
        card.setLatestRevision(revision);
        revision.setCreatedBy(user);
        user.getRevisions().add(revision);

        return getRevisionRepository().saveAndFlush(revision);
    }

    default RevisionEdit givenRevisionEdit() {
        Revision revision = givenRevision();
        RevisionEdit revisionEdit = new RevisionEdit();
        revisionEdit.setTextFront(FRONT_TEXT);
        revisionEdit.setTextBack(BACK_TEXT);
        revisionEdit.setRevision(revision);
        revision.setRevisionEdit(revisionEdit);

        return getRevisionEditRepository().saveAndFlush(revisionEdit);
    }

    default Category givenCategory() {
        Category category = new Category();
        Category parent = new Category();
        User user = givenApplicationUser();
        category.setCreatedBy(user);
        category.setName(CATEGORY_NAME);
        parent.setName(PARENT_CATEGORY_NAME);

        parent = getCategoryRepository().saveAndFlush(parent);
        category.setParent(parent);
        return  getCategoryRepository().saveAndFlush(category);
    }

    Long DECK_ID = 0L;
    Long CARD_ID = 1L;
    Long REVISION_ID = 2L;
    Long REVISION_EDIT_ID = 3L;
    LocalDateTime CREATED_AT = LocalDateTime.now();
    LocalDateTime UPDATED_AT = LocalDateTime.now();

    default User getUnconnectedSampleUser() {
        User user = new User();
        user.setOAuthId(OAUTH_ID);
        user.setUsername(USER_NAME);
        user.setAdmin(false);
        user.setEnabled(true);
        user.setDescription("some user");
        return user;
    }

    default Deck getUnconnectedSampleDeck() {
        Deck deck = new Deck();
        deck.setId(DECK_ID);
        deck.setName(DECK_NAME);
        deck.setCreatedAt(CREATED_AT);
        deck.setUpdatedAt(UPDATED_AT);
        return deck;
    }

    default Card getUnconnectedSampleCard() {
        Card card = new Card();
        card.setId(CARD_ID);
        card.setCreatedAt(CREATED_AT);
        return card;
    }

    default Revision getUnconnectedSampleRevision() {
        Revision revision = new Revision();
        revision.setId(REVISION_ID);
        revision.setMessage(REVISION_MESSAGE);
        revision.setCreatedAt(CREATED_AT);
        return revision;
    }

    default RevisionEdit getUnconnectedSampleRevisionEdit() {
        RevisionEdit revisionEdit = new RevisionEdit();
        revisionEdit.setId(REVISION_EDIT_ID);
        revisionEdit.setTextFront(FRONT_TEXT);
        revisionEdit.setTextBack(BACK_TEXT);
        return revisionEdit;
    }

    default User getSampleUser() {
        return getUnconnectedSampleUser();
    }

    default Deck getSampleDeck() {
        Deck deck = getUnconnectedSampleDeck();
        deck.setCreatedBy(getSampleUser());
        return deck;
    }

    default Card getSampleCard() {
        Card card = getUnconnectedSampleCard();
        card.setDeck(getSampleDeck());
        card.getDeck().getCards().add(card);
        return card;
    }

    default Revision getSampleRevision() {
        Revision revision = getUnconnectedSampleRevision();
        revision.setCreatedBy(getSampleUser());
        revision.getCreatedBy().getRevisions().add(revision);
        revision.setCard(getSampleCard());
        revision.getCard().getRevisions().add(revision);
        revision.getCard().setLatestRevision(revision);
        return revision;
    }

    default RevisionEdit getSampleRevisionEdit() {
        RevisionEdit revisionEdit = getUnconnectedSampleRevisionEdit();
        revisionEdit.setRevision(getSampleRevision());
        revisionEdit.getRevision().setRevisionEdit(revisionEdit);
        return revisionEdit;
    }

    default Matcher<String> validIsoDateTime() {
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
}
