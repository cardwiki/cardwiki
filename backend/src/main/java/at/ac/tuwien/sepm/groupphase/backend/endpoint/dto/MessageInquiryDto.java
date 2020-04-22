package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class MessageInquiryDto {

    @NotNull(message = "Title must not be null")
    @Size(max = 100)
    private String title;

    @NotNull(message = "Summary must not be null")
    @Size(max = 500)
    private String summary;

    @NotNull(message = "Text must not be null")
    @Size(max = 10000)
    private String text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageInquiryDto)) return false;
        MessageInquiryDto that = (MessageInquiryDto) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(summary, that.summary) &&
            Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, summary, text);
    }

    @Override
    public String toString() {
        return "MessageInquiryDto{" +
            "title='" + title + '\'' +
            ", summary='" + summary + '\'' +
            ", text='" + text + '\'' +
            '}';
    }


    public static final class MessageInquiryDtoBuilder {
        private String title;
        private String summary;
        private String text;

        private MessageInquiryDtoBuilder() {
        }

        public static MessageInquiryDtoBuilder aMessageInquiryDto() {
            return new MessageInquiryDtoBuilder();
        }

        public MessageInquiryDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public MessageInquiryDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public MessageInquiryDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public MessageInquiryDto build() {
            MessageInquiryDto messageInquiryDto = new MessageInquiryDto();
            messageInquiryDto.setTitle(title);
            messageInquiryDto.setSummary(summary);
            messageInquiryDto.setText(text);
            return messageInquiryDto;
        }
    }
}