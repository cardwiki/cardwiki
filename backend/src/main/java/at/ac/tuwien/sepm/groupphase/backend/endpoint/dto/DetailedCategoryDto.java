package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class DetailedCategoryDto extends SimpleCategoryDto {

    private User createdBy;
    private Category parent;
    private Set<Category> children;
    private Date createdAt;
    private Date updatedAt;

    public User getCreatedBy() { return createdBy; }

    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Category getParent() { return parent; }

    public void setParent(Category parent) { this.parent = parent; }

    public Set<Category> getChildren() { return children; }

    public void setChildren(Set<Category> children) { this.children = children; }

    public Date getCreatedAt() { return createdAt; }

    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailedCategoryDto)) return false;
        if (!super.equals(o)) return false;
        DetailedCategoryDto that = (DetailedCategoryDto) o;
        return Objects.equals(getCreatedAt(), that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCreatedAt());
    }

    @Override
    public String toString() {
        return "DetailedCategoryDto{" +
            "createdBy=" + createdBy +
            ", parent=" + parent +
            ", children=" + children +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
