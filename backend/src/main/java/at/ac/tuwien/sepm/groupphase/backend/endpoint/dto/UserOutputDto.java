package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserOutputDto {
    private String oAuthId;

    private String username;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isAdmin;

    public UserOutputDto(){
    }

    public UserOutputDto(String oAuthId, String username, String description, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isAdmin) {
        this.oAuthId = oAuthId;
        this.username = username;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAdmin = isAdmin;
    }

    public String setOAuthId() {
        return oAuthId;
    }

    public void setOAuthId(String oauthId) {
        this.oAuthId = oauthId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "UserOutputDto{" +
            "oAuthId='" + oAuthId + '\'' +
            ", username='" + username + '\'' +
            ", description='" + description + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", isAdmin=" + isAdmin +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserOutputDto that = (UserOutputDto) o;
        return isAdmin == that.isAdmin &&
            oAuthId.equals(that.oAuthId) &&
            username.equals(that.username) &&
            description.equals(that.description) &&
            createdAt.equals(that.createdAt) &&
            updatedAt.equals(that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oAuthId, username, description, createdAt, updatedAt, isAdmin);
    }
}
