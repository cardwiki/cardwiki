package at.ac.tuwien.sepm.groupphase.backend.repository.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepositoryCustom;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.time.LocalDateTime;

import java.util.*;


@Repository
public class DeckRepositoryImpl implements DeckRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Long createDeckCopy(Long deckId, User user, Deck deckCopy) {
        // get revisionEdits of latestRevisions
        List<Object[]> result = entityManager.createNativeQuery(
            "SELECT r.TEXT_FRONT, r.TEXT_BACK FROM revision_edits r" +
                " INNER JOIN cards c ON r.REVISION_ID=c.LATEST_REVISION" +
                " WHERE c.DECK_ID=:deckId" +
                " ORDER BY c.ID ASC"
        )
            .setParameter("deckId", deckId)
            .getResultList();

        // create new deck
        entityManager.createNativeQuery("INSERT INTO DECKS(created_at, name, updated_at, created_by)" +
            " VALUES(:now, :name, :now, :createdBy)"
        )
            .setParameter("now", LocalDateTime.now())
            .setParameter("name", deckCopy.getName())
            .setParameter("createdBy", user.getId())
            .executeUpdate();

        BigInteger deckCopyId = (BigInteger) entityManager.createNativeQuery("SELECT scope_identity()").getSingleResult();

        for (Object[] revisionEditResult : result) {
            // create card and initial revision
            entityManager.createNativeQuery(
                "INSERT INTO cards(created_at, deck_id)" +
                    " VALUES(:now, :deckCopyId)"
            )
                .setParameter("now", LocalDateTime.now())
                .setParameter("deckCopyId", deckCopyId)
                .executeUpdate();

            BigInteger cardId = (BigInteger) entityManager.createNativeQuery("SELECT scope_identity()").getSingleResult();

            entityManager.createNativeQuery(
                "INSERT INTO revisions(created_at, message, card_id, created_by)" +
                    " VALUES(:now, :message, :cardId, :createdBy)"
            )
                .setParameter("now", LocalDateTime.now())
                .setParameter("message", String.format("Copied from deck %s.", deckId))
                .setParameter("cardId", cardId)
                .setParameter("createdBy", user.getId())
                .executeUpdate();

            BigInteger revisionId = (BigInteger) entityManager.createNativeQuery("SELECT scope_identity()").getSingleResult();

            entityManager.createNativeQuery("UPDATE cards SET latest_revision=:latestRevision WHERE id=:cardId")
                .setParameter("latestRevision", revisionId)
                .setParameter("cardId", cardId)
                .executeUpdate();

            // add revisionEdit
            entityManager.createNativeQuery(
                "INSERT INTO revision_edits(revision_id, text_front, text_back)" +
                    " VALUES(:revisionId, :textFront, :textBack)"
            )
                .setParameter("revisionId", revisionId)
                .setParameter("textFront", revisionEditResult[0])
                .setParameter("textBack", revisionEditResult[1])
                .executeUpdate();
        }

        // add categories
        entityManager.createNativeQuery(
            "INSERT INTO category_deck" +
                " SELECT category_id, :deckCopyId FROM category_deck"+
                " WHERE deck_id=:deckId"
        )
            .setParameter("deckCopyId", deckCopyId)
            .setParameter("deckId", deckId)
            .executeUpdate();

        return deckCopyId.longValue();
    }
}
