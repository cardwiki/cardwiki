package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.validation.ContentNotNull;
import at.ac.tuwien.sepm.groupphase.backend.validation.NullOrNotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@ContentNotNull
@Entity
@Table(name = "revision_edits")
public class RevisionEdit {
    public static final int MAX_TEXT_SIZE = 1000;

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "revision_id", updatable = false)
    private Revision revision;


    @Size(max = MAX_TEXT_SIZE)
    @NullOrNotBlank
    @Column(name = "text_front", length = MAX_TEXT_SIZE, updatable = false)
    private String textFront;


    @Size(max = MAX_TEXT_SIZE)
    @NullOrNotBlank
    @Column(name = "text_back", length = MAX_TEXT_SIZE, updatable = false)
    private String textBack;

    @Column(name = "image_front", updatable = false)
    private String imageFront;

    @Column(name = "image_back", updatable = false)
    private String imageBack;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    public String getTextFront() {
        return textFront;
    }

    public void setTextFront(String textFront) {
        this.textFront = textFront;
    }

    public String getTextBack() {
        return textBack;
    }

    public void setTextBack(String textBack) {
        this.textBack = textBack;
    }

    public String getImageFront() {
        return imageFront;
    }

    public void setImageFront(String imageFront) {
        this.imageFront = imageFront;
    }

    public String getImageBack() {
        return imageBack;
    }

    public void setImageBack(String imageBack) {
        this.imageBack = imageBack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevisionEdit that = (RevisionEdit) o;
        return Objects.equals(getTextFront(), that.getTextFront()) &&
            Objects.equals(getImageFront(), that.getImageFront()) &&
            Objects.equals(getTextBack(), that.getTextBack()) &&
            Objects.equals(getImageBack(), that.getImageBack());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTextFront(), getImageFront(), getTextBack(), getImageBack());
    }

    @Override
    public String toString() {
        return "RevisionEdit{" +
            "textFront='" + textFront + '\'' +
            ", imageFront='" + imageFront + '\'' +
            ", textBack='" + textBack + '\'' +
            ", imageBack='" + imageBack + '\'' +
            '}';
    }
}