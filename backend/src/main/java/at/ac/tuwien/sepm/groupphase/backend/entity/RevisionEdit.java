package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.validation.NullOrNotBlank;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "revision_edits")
@DiscriminatorValue(Revision.Type.Values.EDIT)
public class RevisionEdit extends Revision {
    public static final int MAX_TEXT_SIZE = 1000;

    @Id
    private Long id;

    @Size(max = MAX_TEXT_SIZE)
    @NullOrNotBlank
    @Column(name = "text_front", length = MAX_TEXT_SIZE, nullable = true, updatable = false)
    private String textFront;

    @Size(max = MAX_TEXT_SIZE)
    @NullOrNotBlank
    @Column(name = "text_back", length = MAX_TEXT_SIZE, nullable = true, updatable = false)
    private String textBack;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_front_id", nullable = true, updatable = false)
    private Image imageFront;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_back_id", nullable = true, updatable = false)
    private Image imageBack;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Image getImageFront() {
        return imageFront;
    }

    public void setImageFront(Image imageFront) {
        this.imageFront = imageFront;
    }

    public Image getImageBack() {
        return imageBack;
    }

    public void setImageBack(Image imageBack) {
        this.imageBack = imageBack;
    }

    @Override
    public String toString() {
        return "RevisionEdit{" +
            "textFront='" + textFront + '\'' +
            ", imageFront='" + imageFront + '\'' +
            ", textFront='" + textFront + '\'' +
            ", textBack='" + textBack + '\'' +
            ", imageBack='" + imageBack + '\'' +
            '}';
    }
}
