package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Card;
import at.ac.tuwien.sepm.groupphase.backend.entity.Revision;
import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import at.ac.tuwien.sepm.groupphase.backend.repository.CardRepository;
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

    private final CardRepository cardRepository;

    public CardDataGenerator(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @PostConstruct
    private void generateCards() {
        long countCards = cardRepository.count();
        for (int i = (int)countCards; i < NUMBER_OF_CARDS_TO_GENERATE; i++) {
            LOGGER.info("Creating card {}", i);
            Card card = new Card();
            Revision revision = new Revision();

            card.setLatestRevision(revision);
            revision.setCard(card);
            revision.setMessage("Test Revision " + i);

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

                card = cardRepository.save(card);
            }
        }
    }

}
