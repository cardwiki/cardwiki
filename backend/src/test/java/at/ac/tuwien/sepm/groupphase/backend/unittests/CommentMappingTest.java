package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CommentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CommentMappingTest extends TestDataGenerator {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void givenComment_whenMapToCommentSimpleDto_thenDtoHasAllProperties() {
        User user = new User();
        user.setUsername("cool-user-2000");
        user.setId(1L);
        Comment comment = new Comment();
        comment.setId(2L);
        comment.setCreatedBy(user);
        comment.setMessage("I found this deck yesteryear. Still amazed");
        comment.setCreatedAt(LocalDateTime.now().minusDays(1L));
        comment.setUpdatedAt(LocalDateTime.now());

        CommentSimpleDto dto = commentMapper.commentToCommentSimpleDto(comment);

        assertAll(
            () -> assertEquals(comment.getId(), dto.getId()),
            () -> assertEquals(comment.getMessage(), dto.getMessage()),
            () -> assertEquals(comment.getCreatedBy().getId(), dto.getCreatedBy().getId()),
            () -> assertEquals(comment.getCreatedBy().getUsername(), dto.getCreatedBy().getUsername()),
            () -> assertEquals(comment.getCreatedAt(), dto.getCreatedAt()),
            () -> assertEquals(comment.getUpdatedAt(), dto.getUpdatedAt())
        );
    }

    @Test
    public void givenCommentInquiryDto_whenMapToComment_thenCommentHasAllProperties() {
        CommentInquiryDto dto = new CommentInquiryDto();
        dto.setMessage("some message");
        Comment comment = commentMapper.commentInquiryDtoToComment(dto);

        assertEquals(dto.getMessage(), comment.getMessage());
    }
}
