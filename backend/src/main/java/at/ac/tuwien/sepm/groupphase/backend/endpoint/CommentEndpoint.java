package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CommentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Comment;
import at.ac.tuwien.sepm.groupphase.backend.service.CommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1")
public class CommentEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentEndpoint(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/decks/{deckId}/comments")
    @ApiOperation(value = "Add a new comment to a deck", authorizations = {@Authorization("user")})
    public CommentSimpleDto create(@PathVariable Long deckId, @Valid @RequestBody CommentInquiryDto commentInquiryDto) {
        LOGGER.info("POST /api/v1/decks/{}/comments body: {}", deckId, commentInquiryDto);
        Comment commentInput = commentMapper.commentInquiryDtoToComment(commentInquiryDto);
        Comment commentOutput = commentService.addCommentToDeck(deckId, commentInput);

        return commentMapper.commentToCommentSimpleDto(commentOutput);
    }

    @GetMapping(value = "/decks/{deckId}/comments")
    @ApiOperation(value = "Get page of comments for a deck")
    public Page<CommentSimpleDto> getCommentsByDeckId(@PathVariable Long deckId, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        LOGGER.info("GET /api/v1/decks/{}/comments", deckId);
        return commentService.findCommentsByDeckId(deckId, pageable)
            .map(commentMapper::commentToCommentSimpleDto);
    }

    @GetMapping(value = "/comments/{commentId}")
    @ApiOperation(value = "Get information about a specific comment")
    public CommentSimpleDto findOne(@PathVariable Long commentId) {
        LOGGER.info("GET /api/v1/comments/{}", commentId);
        return commentMapper.commentToCommentSimpleDto(commentService.findOneOrThrow(commentId));
    }

    @Secured("ROLE_USER")
    @PutMapping(value = "/comments/{commentId}")
    @ApiOperation(value = "Edit a comment", authorizations = {@Authorization("user")})
    public CommentSimpleDto edit(@PathVariable Long commentId, @Valid @RequestBody CommentInquiryDto commentInquiryDto) {
        LOGGER.info("PUT /api/v1/comments/{} body: {}", commentId, commentInquiryDto);
        Comment commentInput = commentMapper.commentInquiryDtoToComment(commentInquiryDto);
        Comment commentOutput = commentService.editComment(commentId, commentInput);

        return commentMapper.commentToCommentSimpleDto(commentOutput);
    }

    @Secured("ROLE_USER")
    @DeleteMapping(value = "/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete a comment", authorizations = {@Authorization("user")})
    public void deleteComment(@PathVariable Long commentId) {
        LOGGER.info("DELETE /api/v1/comments/{}", commentId);
        commentService.deleteComment(commentId);
    }
}
