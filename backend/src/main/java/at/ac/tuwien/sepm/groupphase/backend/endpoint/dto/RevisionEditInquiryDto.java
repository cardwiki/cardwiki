package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.RevisionEdit;
import com.google.common.base.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RevisionEditInquiryDto {

    @NotNull(message = "Front text must not be null")
    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
    private String textFront;

    @NotNull(message = "Back text must not be null")
    @Size(max = RevisionEdit.MAX_TEXT_SIZE)
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
        if (o == null || getClass() != o.getClass()) return false;
        RevisionEditInquiryDto that = (RevisionEditInquiryDto) o;
        return Objects.equal(textFront, that.textFront) &&
            Objects.equal(textBack, that.textBack);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(textFront, textBack);
    }

    @Override
    public String toString() {
        return "RevisionEditInquiryDto{" +
            "textFront='" + textFront + '\'' +
            ", textBack='" + textBack + '\'' +
            '}';
    }
}