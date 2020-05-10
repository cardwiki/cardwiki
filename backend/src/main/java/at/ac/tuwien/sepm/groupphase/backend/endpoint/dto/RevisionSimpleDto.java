package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.google.common.base.Objects;

import java.time.LocalDateTime;

public class RevisionSimpleDto {

    private Long id;
    private String message;
    private String createdBy;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevisionSimpleDto that = (RevisionSimpleDto) o;
        return Objects.equal(id, that.id) &&
            Objects.equal(message, that.message) &&
            Objects.equal(createdBy, that.createdBy) &&
            Objects.equal(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, message, createdBy, createdAt);
    }

    @Override
    public String toString() {
        return "RevisionSimpleDto{" +
            "id=" + id +
            ", message='" + message + '\'' +
            ", createdBy='" + createdBy + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
