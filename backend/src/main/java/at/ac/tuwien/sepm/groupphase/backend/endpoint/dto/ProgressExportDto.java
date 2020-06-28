package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Progress;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgressExportDto {
    private Long cardId;
    private DeckSimpleDto deck;
    private RevisionExportDto latestRevision;
    private LocalDateTime due;
    private Double easinessFactor;
    private Integer interval;
    private Progress.Status status;
}
