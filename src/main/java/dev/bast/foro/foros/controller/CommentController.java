package dev.bast.foro.foros.controller;

import dev.bast.foro.foros.dto.CommentDto;
import dev.bast.foro.foros.service.CommentService;
import dev.bast.foro.usuarios.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<CommentDto>> getCommentsByTopicId(@PathVariable Long topicId) {
        List<CommentDto> comments = commentService.getCommentsByTopicId(topicId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDto commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        CommentDto createdComment = commentService.createComment(
                commentDto, 
                userDetails.getId(), 
                userDetails.getUsername()
        );
        
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        CommentDto existingComment = commentService.getCommentById(id);
        
        // Only allow the creator or admin/moderator to update
        if (!existingComment.getUserId().equals(userDetails.getId()) && 
                !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                       a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        CommentDto updatedComment = commentService.updateComment(id, commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        CommentDto existingComment = commentService.getCommentById(id);
        
        // Only allow the creator or admin/moderator to delete
        if (!existingComment.getUserId().equals(userDetails.getId()) && 
                !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                       a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> banComment(@PathVariable Long id) {
        commentService.banComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentDto>> getCommentsByUserId(@PathVariable Long userId) {
        List<CommentDto> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }
}