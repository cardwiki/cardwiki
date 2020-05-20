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
        return !entityManager.createNativeQuery("WITH LINK(ID, PARENT_ID, LEVEL) AS (" +
            "SELECT ID, PARENT_ID, 0 FROM CATEGORIES WHERE CATEGORIES.ID=:parentId " +
            "UNION ALL " +
            "SELECT CATEGORIES.ID, CATEGORIES.PARENT_ID, LEVEL + 1 " +
            "FROM LINK INNER JOIN CATEGORIES ON LINK.PARENT_ID = CATEGORIES.ID AND " +
            "CATEGORIES.ID!=:parentId) " +
            "SELECT a.NAME FROM CATEGORIES AS a INNER JOIN LINK ON LINK.ID=a.ID " +
            "WHERE LINK.ID=:id")
            .setParameter("parentId", parentId)
            .setParameter("id", id)
            .getResultList()
            .isEmpty();
    }
}
