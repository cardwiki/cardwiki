package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

    ApplicationUserRepository getApplicationUserRepository();
    DeckRepository getDeckRepository();
    CardRepository getCardRepository();
    RevisionRepository getRevisionRepository();
    RevisionEditRepository getRevisionEditRepository();
    CategoryRepository getCategoryRepository();

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";

    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

    String OAUTH_ID = "Fake Id";
    String USER_NAME = "Test User";
    String DECK_NAME = "Test Deck";
    String REVISION_MESSAGE = "Test Revision";
    String FRONT_TEXT = "Test Front";
    String BACK_TEXT = "Test Back";
    String CATEGORY_NAME = "Test Category";
    String PARENT_CATEGORY_NAME = "Test Parent Category";

    default User givenApplicationUser() {
        User user = new User();
        user.setUsername(USER_NAME);
        user.setOAuthId(OAUTH_ID);
        return getApplicationUserRepository().saveAndFlush(user);
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
        category.setParent(parent);

        getCategoryRepository().saveAndFlush(parent);
        return  getCategoryRepository().saveAndFlush(category);
    }
}
