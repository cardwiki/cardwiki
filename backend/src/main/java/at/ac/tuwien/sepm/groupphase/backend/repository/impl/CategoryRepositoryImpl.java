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
    public boolean parentExistsWithId(Long id, Long parentId) {
   /*     Category result = new Category(parentId);
        do {
            Long currentChild;
            result = entityManager.find(Category.class, currentChild = result.getId());
            if (currentChild.equals(id)) return true;
        } while ((result = result.getParent()) != null);
        return false; */
    Category result = entityManager.find(Category.class, parentId);
    while ((result = result.getParent()) != null) {
        if (result.getId().equals(id)) return true;
    }
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
