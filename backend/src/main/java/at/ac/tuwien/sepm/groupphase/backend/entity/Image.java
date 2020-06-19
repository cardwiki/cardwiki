package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "images")
public class Image {
    @Id
    private String filename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by", nullable = false, updatable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "imageFront")
    private Set<RevisionEdit> frontSides = new HashSet<>();

    @OneToMany(mappedBy = "imageBack")
    private Set<RevisionEdit> backSides = new HashSet<>();

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<RevisionEdit> getFrontSides() {
        return frontSides;
    }

    public void setFrontSides(Set<RevisionEdit> frontSides) {
        this.frontSides = frontSides;
    }

    public Set<RevisionEdit> getBackSides() {
        return backSides;
    }

    public void setBackSides(Set<RevisionEdit> backSides) {
        this.backSides = backSides;
    }

    @Override
    public String toString() {
        return "Image{" +
            ", filename='" + filename + '\'' +
            ", createdBy=" + createdBy +
            ", createdAt=" + createdAt +
            '}';
    }
}
