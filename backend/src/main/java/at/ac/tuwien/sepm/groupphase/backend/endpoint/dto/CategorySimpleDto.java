package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.Objects;

public class CategorySimpleDto {

    private Long id;
    private String name;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategorySimpleDto)) return false;
        CategorySimpleDto that = (CategorySimpleDto) o;
        return getId().equals(that.getId()) &&
            getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "SimpleCategoryDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
