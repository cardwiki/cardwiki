package at.ac.tuwien.sepm.groupphase.backend.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by", nullable = false, updatable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "imageFront")
    private Set<RevisionEdit> frontSides;

    @OneToMany(mappedBy = "imageBack")
    private Set<RevisionEdit> backSides;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return getId().equals(image.getId()) &&
            getFilename().equals(image.getFilename()) &&
            getCreatedBy().equals(image.getCreatedBy()) &&
            getCreatedAt().equals(image.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFilename(), getCreatedBy(), getCreatedAt());
    }

    @Override
    public String toString() {
        return "Image{" +
            "id=" + id +
            ", filename='" + filename + '\'' +
            ", createdBy=" + createdBy +
            ", createdAt=" + createdAt +
            '}';
    }
}
