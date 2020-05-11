package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class UserInputDto {
    private String oAuthId;

    @NotNull
    private String username;

    @NotNull
    private String description;

    private boolean isAdmin;

    public UserInputDto(){
    }

    public UserInputDto(String oAuthId, @NotNull String username, @NotNull String description, boolean isAdmin) {
        this.oAuthId = oAuthId;
        this.username = username;
        this.description = description;
        this.isAdmin = isAdmin;
    }

    public String getOAuthId() {
        return oAuthId;
    }

    public void setOAuthId(String oAuthId) {
        this.oAuthId = oAuthId;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "UserInputDto{" +
            "oAuthId='" + oAuthId + '\'' +
            ", username='" + username + '\'' +
            ", description='" + description + '\'' +
            ", isAdmin=" + isAdmin +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInputDto that = (UserInputDto) o;
        return isAdmin == that.isAdmin &&
            oAuthId.equals(that.oAuthId) &&
            username.equals(that.username) &&
            description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oAuthId, username, description, isAdmin);
    }
}
