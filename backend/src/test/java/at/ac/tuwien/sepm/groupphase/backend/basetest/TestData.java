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
    String UTF_16_SAMPLE_TEXT = "ユ简크로أفضل البحوثΣὲ γνДесแผ∮E⋅∞∑çéèñé";

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
}
