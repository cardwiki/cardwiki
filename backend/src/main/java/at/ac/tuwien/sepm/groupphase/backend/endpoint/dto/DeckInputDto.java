package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(value = { "createdBy" })
public class DeckInputDto {
    private String name;
    private String createdBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeckInputDto)) return false;
        DeckInputDto that = (DeckInputDto) o;
        return Objects.equals(name, that.name) &&
            Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, createdBy);
    }

    @Override
    public String toString() {
        return "DeckInputDto{" +
            "name='" + name + '\'' +
            ", createdBy='" + createdBy + '\'' +
            '}';
    }
}
