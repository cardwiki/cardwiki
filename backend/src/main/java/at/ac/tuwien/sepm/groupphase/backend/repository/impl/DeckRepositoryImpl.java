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
    public List<RevisionEdit> getRevisionEditsByDeckId(Long deckId) {
       List<Object[]> result = entityManager.createNativeQuery(
            "SELECT * FROM revision_edits" +
                " WHERE REVISION_ID IN (" +
                " SELECT LATEST_REVISION FROM cards" +
                " WHERE DECK_ID=:deckId)"
        )
            .setParameter("deckId", deckId)
            .getResultList();

        List<RevisionEdit> revisionEdits = new ArrayList<>(result.size());
        for (Object[] item : result) {
            RevisionEdit revisionEdit = new RevisionEdit();
            BigInteger id = (BigInteger)item[0];
            revisionEdit.setId(id.longValue());
            revisionEdit.setTextFront((String) item[1]);
            revisionEdit.setTextBack((String) item[2]);
            revisionEdits.add(revisionEdit);
        }
        return revisionEdits;
    }

    @Override
    @Transactional
    public void copyCategoriesOfDeck(Long deckId, Long copyId) {
        List<Object[]> result = entityManager.createNativeQuery(
            "SELECT * FROM categories" +
                " WHERE id IN (" +
                " SELECT category_id FROM category_deck" +
                " WHERE deck_id=:deckId)"
        )
            .setParameter("deckId", deckId)
            .getResultList();

        for (Object[] item : result) {
            entityManager.createNativeQuery(
                "INSERT INTO category_deck(category_id, deck_id)" +
                    " VALUES(:categoryId, :deckId)"
            )
                .setParameter("categoryId", item[0])
                .setParameter("deckId", copyId)
                .executeUpdate();
        }
    }

    @Override
    @Transactional
    public void addCardCopiesToDeckCopy(Long deckId, List<RevisionEdit> revisionEdits, User user) {
        for (RevisionEdit revisionEdit : revisionEdits) {
            entityManager.createNativeQuery(
                "INSERT INTO cards(created_at, deck_id)" +
                    " VALUES(:now, :deckId)"
            )
                .setParameter("now", LocalDateTime.now())
                .setParameter("deckId", deckId)
                .executeUpdate();

            BigInteger cardId = (BigInteger) entityManager.createNativeQuery("SELECT scope_identity()").getSingleResult();

            entityManager.createNativeQuery(
                "INSERT INTO revisions(created_at, message, card_id, created_by)" +
                    " VALUES(:now, :message, :cardId, :createdBy)"
            )
                .setParameter("now", LocalDateTime.now())
                .setParameter("message", String.format("Copied from deck %s.", deckId))
                .setParameter("cardId", cardId)
                .setParameter("createdBy", user)
                .executeUpdate();

            BigInteger revisionId = (BigInteger) entityManager.createNativeQuery("SELECT scope_identity()").getSingleResult();

            entityManager.createNativeQuery("UPDATE cards SET latest_revision=:latestRevision WHERE id=:cardId")
                .setParameter("latestRevision", revisionId)
                .setParameter("cardId", cardId)
                .executeUpdate();

            entityManager.createNativeQuery(
                "INSERT INTO revision_edits(revision_id, text_front, text_back)" +
                    " VALUES(:revisionId, :textFront, :textBack)"
            )
                .setParameter("revisionId", revisionId)
                .setParameter("textFront", revisionEdit.getTextFront())
                .setParameter("textBack", revisionEdit.getTextBack())
                .executeUpdate();
        }
    }
}
