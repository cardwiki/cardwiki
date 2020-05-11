package at.ac.tuwien.sepm.groupphase.backend.repository.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Category;
import at.ac.tuwien.sepm.groupphase.backend.repository.CategoryRepositoryCustom;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Repository
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public boolean childExistsWithId(Long id) {
  /*        String sql = "WITH LINK(id, parent_id, level) AS (" +
            "SELECT id, parent_id, 0 FROM Category WHERE id=:id" +
            " UNION ALL" +
            " SELECT id, parent_id, LEVEL + 1 FROM LINK" +
            " INNER JOIN Category ON link.id=Category.parent_id)" +
            " SELECT * FROM link WHERE id=:id";

           List<Object> result = entityManager.createQuery(sql)
            .setParameter("id", id)
            .getResultList();

        Long currentChild;
        Category result = entityManager.find(Category.class, id);
        while (result.getParent() != null) {
            currentChild = result.getParent().getId();
            result = entityManager.find(Category.class, currentChild);
            if (currentChild == id) return true;
        } */
        return false;
    }

    @Override
    public void updateCategory(Long id, Category category) {
        String name = category.getName();
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        String sql = "UPDATE Category c SET c.name=:name, c.parent.id=:parentId WHERE c.id=:id";
        entityManager.createQuery(sql)
            .setParameter("name", name)
            .setParameter("parentId", parentId)
            .setParameter("id", id)
            .executeUpdate();
    }
}
