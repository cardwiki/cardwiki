package at.ac.tuwien.sepm.groupphase.backend.repository.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.DeckRepositoryCustom;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.IntStream;


@Repository
public class DeckRepositoryImpl implements DeckRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    @Transactional
    public Deck createDeckCopy(Long deckId, User user, Deck deckCopy) {
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

        entityManager.createNativeQuery(
            "INSERT INTO cards(created_at, deck_id)" +
                " SELECT current_timestamp, :deckCopyId FROM cards c" +
                " WHERE c.DECK_ID=:deckId"
        )
            .setParameter("deckCopyId", deckCopyId)
            .setParameter("deckId", deckId)
            .executeUpdate();

        entityManager.createNativeQuery(
            "INSERT INTO revisions(created_at, message, created_by, card_id)" +
                " SELECT current_timestamp, :message, :createdBy, c.ID FROM cards c" +
                " WHERE DECK_ID=:deckCopyId"
        )
            .setParameter("message", String.format("Copied from deck %s.", deckId))
            .setParameter("createdBy", user.getId())
            .setParameter("deckCopyId", deckCopyId)
            .executeUpdate();

        entityManager.createNativeQuery("UPDATE cards AS c SET LATEST_REVISION=(" +
            "SELECT ID FROM revisions WHERE CARD_ID=c.ID)" +
            " WHERE c.DECK_ID=:deckCopyId")
            .setParameter("deckCopyId", deckCopyId)
            .executeUpdate();

        entityManager.createNativeQuery(
            "INSERT INTO revision_edits(revision_id, text_front, text_back)" +
                " SELECT r.ID, 'dummy', 'dummy' FROM revisions r" +
                " INNER JOIN cards c ON r.ID=c.LATEST_REVISION" +
                " WHERE c.DECK_ID=:deckCopyId" +
                " ORDER BY c.ID ASC"
        )
            .setParameter("deckCopyId", deckCopyId)
            .executeUpdate();

        List<BigInteger> revisionIds = entityManager.createNativeQuery(
            "SELECT LATEST_REVISION FROM cards WHERE DECK_ID=:deckCopyId"
        )
            .setParameter("deckCopyId", deckCopyId)
            .getResultList();

        IntStream.range(0, revisionIds.size())
            .forEach(i -> entityManager.createNativeQuery("UPDATE revision_edits r SET TEXT_FRONT=:textFront, TEXT_BACK=:textBack" +
                " WHERE r.REVISION_ID=:revisionId")
                .setParameter("textFront", result.get(i)[0])
                .setParameter("textBack", result.get(i)[1])
                .setParameter("revisionId", revisionIds.get(i))
                .executeUpdate()
        );

        // add categories
        entityManager.createNativeQuery(
            "INSERT INTO category_deck" +
                " SELECT category_id, :deckCopyId FROM category_deck" +
                " WHERE deck_id=:deckId"
        )
            .setParameter("deckCopyId", deckCopyId)
            .setParameter("deckId", deckId)
            .executeUpdate();

        // query result
        Deck deck = new Deck();
        deck.setId(deckCopyId.longValue());

        List<Object[]> deckResult = entityManager.createNativeQuery(
            "SELECT NAME, CREATED_BY, CREATED_AT, UPDATED_AT FROM decks WHERE ID=:deckCopyId")
            .setParameter("deckCopyId", deckCopyId)
            .getResultList();

        deck.setName((String) deckResult.get(0)[0]);
        BigInteger createdById = (BigInteger)deckResult.get(0)[1];
        deck.setCreatedBy(entityManager.createQuery("SELECT u FROM User u WHERE u.id=:userId", User.class)
            .setParameter("userId", createdById.longValue())
            .getResultList().get(0)
        );
        Timestamp createdAt = (Timestamp) deckResult.get(0)[2];
        deck.setCreatedAt(createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        Timestamp updatedAt = (Timestamp) deckResult.get(0)[3];
        deck.setUpdatedAt(updatedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        deck.setCategories(new HashSet<>());
        deck.getCategories().addAll(entityManager.createQuery(
            "SELECT c FROM Category c INNER JOIN c.decks d WHERE d.id=:deckCopyId")
            .setParameter("deckCopyId", deckCopyId.longValue())
            .getResultList());

        return deck;
    }
}
