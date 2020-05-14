package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CategoryDetailedDto extends CategorySimpleDto {

    private String createdBy;
    private CategorySimpleDto parent;
    private List<CategorySimpleDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DeckSimpleDto> decks;

    public List<DeckSimpleDto> getDecks() { return decks; }

    public void setDecks(List<DeckSimpleDto> decks) { this.decks = decks; }

    public String getCreatedBy() { return createdBy; }

    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public CategorySimpleDto getParent() { return parent; }

    public void setParent(CategorySimpleDto parent) { this.parent = parent; }

    public List<CategorySimpleDto> getChildren() { return children; }

    public void setChildren(List<CategorySimpleDto> children) { this.children = children; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryDetailedDto)) return false;
        if (!super.equals(o)) return false;
        CategoryDetailedDto that = (CategoryDetailedDto) o;
        return getCreatedAt().equals(that.getCreatedAt());
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
 //            ", children=" + children +
            ", decks=" + decks +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
