package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.Objects;

public class SimpleCategoryDto {

    private Long id;
    private String name;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleCategoryDto)) return false;
        SimpleCategoryDto that = (SimpleCategoryDto) o;
        return getId().equals(that.getId()) &&
            getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "SimpleCategoryDto {" +
            "id=" + id +
            ", name=" + name +
            "}";
    }

    public static final class SimpleCategoryDtoBuilder {
        private Long id;
        private String name;

        private SimpleCategoryDtoBuilder() {
        }

        public static SimpleCategoryDto.SimpleCategoryDtoBuilder aSimpleCategoryDto() {
            return new SimpleCategoryDto.SimpleCategoryDtoBuilder();
        }

        public SimpleCategoryDto.SimpleCategoryDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SimpleCategoryDto.SimpleCategoryDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SimpleCategoryDto build() {
            SimpleCategoryDto simpleCategoryDto = new SimpleCategoryDto();
            simpleCategoryDto.setId(id);
            simpleCategoryDto.setName(name);
            return simpleCategoryDto;
        }
    }
}
