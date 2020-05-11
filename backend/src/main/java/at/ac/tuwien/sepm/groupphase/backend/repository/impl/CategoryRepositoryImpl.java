package at.ac.tuwien.sepm.groupphase.backend.repository.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public boolean childExistsWithId(Long id) {
        String sql = "WITH LINK(ID, PARENT_ID, LEVEL) AS (" +
            "SELECT ID, PARENT_ID, 0 FROM CATEGORY WHERE ID=" + id +
            " UNION ALL" +
            " SELECT CATEGORY.ID, PARENT_ID, LEVEL + 1 FROM LINK" +
            " INNER JOIN CATEGORY ON LINK.ID=CATEGORY.PARENT_ID)" +
            " SELECT * FROM LINK WHERE ID=" + id;

        List<Object> result = this.entityManager.createQuery(sql).getResultList();
        return result.size() == 1;
    }

    @Override
    @Transactional
    public void updateCategory(Long id, Category category) {
        String name = category.getName();
        Long parentId = category.getParent().getId();
        String sql = "UPDATE Category c SET c.name=:name, c.parent.id=:parentId WHERE c.id=:id";
        Query query = entityManager.createQuery(sql);
        query.setParameter("name", name);
        query.setParameter("parentId", parentId);
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
