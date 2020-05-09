package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Category;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class DetailedCategoryDto extends SimpleCategoryDto {

 //   private ApplicationUser createdBy;
    private Category parent;
    private Set<Category> children;
    private Date createdAt;
    private Date updatedAt;

//    public ApplicationUser getCreatedBy() { return createdBy; }

//    public void setCreatedBy(ApplicationUser createdBy) { this.createdBy = createdBy; }

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
 //           "createdBy=" + createdBy +
            ", parent=" + parent +
            ", children=" + children +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }

    public static final class DetailedCategoryDtoBuilder {
        private Long id;
        private String name;
  //      private ApplicationUser createdBy;
        private Category parent;
        private Set<Category> children;
        private Date createdAt;
        private Date updatedAt;

        private DetailedCategoryDtoBuilder() {
        }

        public static DetailedCategoryDto.DetailedCategoryDtoBuilder aDetailedCategoryDto() {
            return new DetailedCategoryDto.DetailedCategoryDtoBuilder();
        }

        public DetailedCategoryDto.DetailedCategoryDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailedCategoryDto.DetailedCategoryDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }
  /*      public DetailedCategoryDto.DetailedCategoryDtoBuilder withCreatedBy(ApplicationUser createdBy) {
            this.createdBy = createdBy;
            return this;
        } */

        public DetailedCategoryDto.DetailedCategoryDtoBuilder withParent(Category parent) {
            this.parent = parent;
            return this;
        }

        public DetailedCategoryDto.DetailedCategoryDtoBuilder withChildren(Set<Category> children) {
            this.children = children;
            return this;
        }

        public DetailedCategoryDto.DetailedCategoryDtoBuilder withCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DetailedCategoryDto.DetailedCategoryDtoBuilder withUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DetailedCategoryDto build() {
            DetailedCategoryDto detailedCategoryDto = new DetailedCategoryDto();
            detailedCategoryDto.setId(id);
            detailedCategoryDto.setName(name);
 //           detailedCategoryDto.setCreatedBy(createdBy);
            detailedCategoryDto.setParent(parent);
            detailedCategoryDto.setChildren(children);
            detailedCategoryDto.setCreatedAt(createdAt);
            detailedCategoryDto.setUpdatedAt(updatedAt);
            return detailedCategoryDto;
        }
    }
}
