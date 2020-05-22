package at.ac.tuwien.sepm.groupphase.backend.repository.impl;

import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public boolean ancestorExistsWithId(Long id, Long parentId) {
        return !entityManager.createNativeQuery(
            "WITH link(id, parent_id, level) AS (" +
                "SELECT id, parent_id, 0 FROM categories WHERE categories.id=:parentId " +
                "UNION ALL " +
                "SELECT categories.id, categories.parent_id, LEVEL + 1 " +
                "FROM link INNER JOIN categories ON link.parent_id = categories.id " +
                "AND categories.id!=:parentId" +
            ") " +
            "SELECT id FROM link WHERE link.id=:id")
            .setParameter("parentId", parentId)
            .setParameter("id", id)
            .getResultList()
            .isEmpty();
    }
}
