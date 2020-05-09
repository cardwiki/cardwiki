package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class DeckDto {
    private Long id;
    private String name;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //private List<SimpleCategoryDto> categories;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    //public List<SimpleCategoryDto> getCategories() {
    //    return categories;
    //}

    //public void setCategories(List<SimpleCategoryDto> categories) {
    //    this.categories = categories;
    //}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeckDto)) return false;
        DeckDto that = (DeckDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt); //&&
            //Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createdBy, createdAt, updatedAt/*, categories*/);
    }

    @Override
    public String toString() {
        return "DeckDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", createdBy='" + createdBy + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            //", categories=" + categories +
            '}';
    }

    public static final class DeckDtoBuilder {
        private Long id;
        private String name;
        private String createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static DeckDtoBuilder aDeckDto() {
            return new DeckDtoBuilder();
        }

        public DeckDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DeckDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DeckDtoBuilder withCreatedBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public DeckDtoBuilder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DeckDtoBuilder withUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DeckDto build() {
            DeckDto deckDto = new DeckDto();
            deckDto.setId(id);
            deckDto.setName(name);
            deckDto.setCreatedBy(createdBy);
            deckDto.setCreatedAt(createdAt);
            deckDto.setUpdatedAt(updatedAt);
            return deckDto;
        }
    }
}
