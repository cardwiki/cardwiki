package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;

    @Id
    private String oAuthId;

    @Column(nullable = false, unique = true, length = MAX_USERNAME_LENGTH)
    private String username;

    @Column(nullable = false)
    private boolean admin;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<Revision> revisions = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String oAuthId, String username, boolean admin, boolean enabled, String description) {
        this.oAuthId = oAuthId;
        this.username = username;
        this.admin = admin;
        this.enabled = enabled;
        this.description = description;
    }

    public void dismissRevision(Revision revision) {
        if (!revisions.remove(revision))
            throw new NoSuchElementException("Tried to dismiss revision which is not yet associated with user");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getOAuthId() {
        return oAuthId;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void setOAuthId(String oauthId) {
        this.oAuthId = oauthId;
    }

    public Set<Revision> getRevisions() {
        return revisions;
    }

    public void setRevisions(Set<Revision> revisions) {
        this.revisions = revisions;
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

    @Override
    public String toString() {
        return "ApplicationUser{" +
            "oAuthId='" + oAuthId + '\'' +
            ", username='" + username + '\'' +
            ", admin=" + admin +
            ", enabled=" + enabled +
            ", description=" + description +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
