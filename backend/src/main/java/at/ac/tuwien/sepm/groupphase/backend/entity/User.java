package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users",
    uniqueConstraints = {@UniqueConstraint(
        columnNames = "username",
        name = User.CONSTRAINT_USERNAME_UNIQUE
    ), @UniqueConstraint(
        columnNames = "authId",
        name = User.CONSTRAINT_AUTHID_UNIQUE
    )})
public class User {
    public static final int MAX_USERNAME_LENGTH = 20;
    public static final int MAX_DESCRIPTION_LENGTH = 5000;
    public static final String CONSTRAINT_USERNAME_UNIQUE = "USERNAME_UNIQUE";
    public static final String CONSTRAINT_AUTHID_UNIQUE = "AUTHID_UNIQUE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String authId;

    @Column(nullable = false, length = MAX_USERNAME_LENGTH)
    private String username;

    @Column(nullable = false)
    private Boolean admin;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Boolean deleted;

    private String reason;

    @Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<Revision> revisions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "favoredBy")
    private Set<Deck> favorites = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User(Long id) {
        this.id = id;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isEnabled(){
        return enabled;
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

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Set<Deck> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<Deck> favorites) {
        this.favorites = favorites;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", authId='" + authId + '\'' +
            ", username='" + username + '\'' +
            ", admin=" + admin +
            ", enabled=" + enabled +
            ", deleted=" + deleted +
            ", reason='" + reason + '\'' +
            ", description='" + description + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
