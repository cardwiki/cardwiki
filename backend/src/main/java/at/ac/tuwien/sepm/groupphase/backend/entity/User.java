package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
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
    private boolean admin;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false, length = MAX_DESCRIPTION_LENGTH)
    private String description;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<Revision> revisions = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "deck_id")
    )
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

    public void dismissRevision(Revision revision) {
        if (!revisions.remove(revision))
            throw new NoSuchElementException("Tried to dismiss revision which is not yet associated with user");
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

    public boolean isAdmin() {
        return admin;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
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

    public Set<Deck> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<Deck> favorites) {
        this.favorites = favorites;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", authId='" + authId + '\'' +
            ", username='" + username + '\'' +
            ", admin=" + admin +
            ", enabled=" + enabled +
            ", description='" + description + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
