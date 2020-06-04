package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@NoRepositoryBean
public interface DeckRepositoryCustom {

    List<RevisionEdit> getRevisionEditsByDeckId(Long deckId);

    void copyCategoriesOfDeck(Long deckId, Long copyId);

    void addCardCopiesToDeckCopy(Long deckId, List<RevisionEdit> revisionEdits, User user);
}
