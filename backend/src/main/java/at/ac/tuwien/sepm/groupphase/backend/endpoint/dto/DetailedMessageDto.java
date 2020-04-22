package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class DetailedMessageDto extends SimpleMessageDto {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailedMessageDto)) return false;
        if (!super.equals(o)) return false;
        DetailedMessageDto that = (DetailedMessageDto) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "DetailedMessageDto{" +
            "text='" + text + '\'' +
            '}';
    }


    public static final class DetailedMessageDtoBuilder {
        private Long id;
        private LocalDateTime publishedAt;
        private String text;
        private String title;
        private String summary;

        private DetailedMessageDtoBuilder() {
        }

        public static DetailedMessageDtoBuilder aDetailedMessageDto() {
            return new DetailedMessageDtoBuilder();
        }

        public DetailedMessageDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailedMessageDtoBuilder withPublishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public DetailedMessageDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public DetailedMessageDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public DetailedMessageDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public DetailedMessageDto build() {
            DetailedMessageDto detailedMessageDto = new DetailedMessageDto();
            detailedMessageDto.setId(id);
            detailedMessageDto.setPublishedAt(publishedAt);
            detailedMessageDto.setText(text);
            detailedMessageDto.setTitle(title);
            detailedMessageDto.setSummary(summary);
            return detailedMessageDto;
        }
    }
}