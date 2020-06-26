package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserExportDto {
    private UserDetailsDto user;
    private List<DeckSimpleDto> favorites;
    private List<DeckSimpleDto> decks;
    private List<CategorySimpleDto> categories;
    private List<RevisionExportDto> revisions;
    private List<CommentExportDto> comments;
    private List<ImageDto> images;
    private List<ProgressExportDto> progress;
}
