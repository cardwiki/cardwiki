package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    @Max(200) @NotNull
    @Pattern(regexp="^[a-zA-Z0-9]+[a-zA-Z0-9 \\\\/\\\\-\\\\.\\\\,]+$",
        message="Invalid String: First character not alphanumeric or contains forbidden characters.")
    private String name;

    @ManyToOne()
    @JoinColumn(name="created_by", referencedColumnName="id", nullable = false, updatable = false)
    private ApplicationUser createdBy;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Category parent;

    @Transient
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<Category> children = new HashSet<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    public Category() {
    }

    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public Category(Long id, String name, ApplicationUser createdBy, Category parent, Set<Category> children,
                    Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.parent = parent;
        this.children = children;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplicationUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ApplicationUser createdBy) {
        this.createdBy = createdBy;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return getId().equals(category.getId()) &&
            getName().equals(category.getName()) &&
            getParent().equals(category.getParent()) &&
            Objects.equals(getChildren(), category.getChildren());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getParent(), getChildren());
    }

    @Override
    public String toString() {
        return "Category: { id: " + id + ", name: " + name + ", parent: " + parent + " }";
    }
}
