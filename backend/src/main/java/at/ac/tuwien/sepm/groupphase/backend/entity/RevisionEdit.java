package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "revision_edits")
@DiscriminatorValue("edit")
public class RevisionEdit extends Revision {
    public static final int MAX_TEXT_SIZE = 1000;

    @Size(max = MAX_TEXT_SIZE)
    @NotBlank
    @Column(nullable = false, name = "text_front", length = MAX_TEXT_SIZE, updatable = false)
    private String textFront;

    @Size(max = MAX_TEXT_SIZE)
    @NotBlank
    @Column(nullable = false, name = "text_back", length = MAX_TEXT_SIZE, updatable = false)
    private String textBack;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RevisionEdit)) return false;
        RevisionEdit edit = (RevisionEdit) o;
        return Objects.equals(textFront, edit.textFront) &&
            Objects.equals(textBack, edit.textBack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textFront, textBack);
    }

    @Override
    public String toString() {
        return "RevisionEdit{" +
            ", textFront='" + textFront + '\'' +
            ", textBack='" + textBack + '\'' +
            '}';
    }
}