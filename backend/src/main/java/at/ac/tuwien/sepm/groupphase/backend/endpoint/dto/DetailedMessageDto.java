package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class DetailedMessageDto extends SimpleMessageDto {

    private String text;
}