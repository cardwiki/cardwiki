package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides repositories to the TestData interface
 */
public abstract class TestDataGenerator implements TestData {
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

    @Override
    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Override
    public DeckRepository getDeckRepository() {
        return deckRepository;
    }

    @Override
    public CardRepository getCardRepository() {
        return cardRepository;
    }

    @Override
    public RevisionRepository getRevisionRepository() {
        return revisionRepository;
    }

    @Override
    public RevisionEditRepository getRevisionEditRepository() {
        return revisionEditRepository;
    }

    @Override
    public CategoryRepository getCategoryRepository() { return categoryRepository; }
}
