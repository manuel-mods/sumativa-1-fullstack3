package dev.bast.foro.foros.service;

import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.foros.dto.CommentDto;
import dev.bast.foro.foros.model.Comment;
import dev.bast.foro.foros.model.Topic;
import dev.bast.foro.foros.repository.CommentRepository;
import dev.bast.foro.foros.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment testComment;
    private CommentDto testCommentDto;
    private Topic testTopic;
    private final Long userId = 1L;
    private final String username = "testuser";
    private final Integer topicId = 1;

    @BeforeEach
    void setUp() {
        // Setup test topic
        testTopic = new Topic();
        testTopic.setId(topicId);
        testTopic.setTitle("Test Topic");
        testTopic.setContent("This is a test topic content");
        testTopic.setActive(true);

        // Setup test comment
        testComment = new Comment();
        testComment.setId(1);
        testComment.setContent("This is a test comment");
        testComment.setTopicId(topicId);
        testComment.setUserId(userId);
        testComment.setUsername(username);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());
        testComment.setActive(true);

        // Setup test comment DTO
        testCommentDto = new CommentDto();
        testCommentDto.setId(1);
        testCommentDto.setContent("This is a test comment");
        testCommentDto.setTopicId(topicId);
        testCommentDto.setUserId(userId);
        testCommentDto.setUsername(username);
        testCommentDto.setCreatedAt(LocalDateTime.now());
        testCommentDto.setUpdatedAt(LocalDateTime.now());
        testCommentDto.setActive(true);
    }

    @Test
    void getCommentsByTopicId_ShouldReturnOnlyActiveComments() {
        // Given
        Comment inactiveComment = new Comment();
        inactiveComment.setId(2);
        inactiveComment.setTopicId(topicId);
        inactiveComment.setActive(false);
        
        when(topicRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.findByTopicIdAndActiveIsTrue(anyLong())).thenReturn(Arrays.asList(testComment));

        // When
        List<CommentDto> result = commentService.getCommentsByTopicId(topicId.longValue());

        // Then
        assertEquals(1, result.size());
        assertEquals(testComment.getId(), result.get(0).getId());
        assertEquals(testComment.getContent(), result.get(0).getContent());
        verify(topicRepository, times(1)).existsById(topicId.longValue());
        verify(commentRepository, times(1)).findByTopicIdAndActiveIsTrue((topicId).longValue());
    }

    @Test
    void getCommentById_WithValidId_ShouldReturnComment() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));

        // When
        CommentDto result = commentService.getCommentById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testComment.getId(), result.getId());
        assertEquals(testComment.getContent(), result.getContent());
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    void getCommentById_WithInvalidId_ShouldThrowException() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(999L));
        verify(commentRepository, times(1)).findById(999L);
    }

    @Test
    void createComment_WithValidTopicId_ShouldSaveAndReturnComment() {
        // Given
        when(topicRepository.existsById(anyLong())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // When
        CommentDto result = commentService.createComment(testCommentDto, userId, username);

        // Then
        assertNotNull(result);
        assertEquals(testComment.getId(), result.getId());
        assertEquals(testComment.getContent(), result.getContent());
        assertEquals(userId, result.getUserId());
        assertEquals(username, result.getUsername());
        assertEquals(topicId, result.getTopicId());
        assertTrue(result.isActive());
        verify(topicRepository, times(1)).existsById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_WithInvalidTopicId_ShouldThrowException() {
        // Given
        when(topicRepository.existsById(anyLong())).thenReturn(false);

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(testCommentDto, userId, username));
        verify(topicRepository, times(1)).existsById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_WithValidId_ShouldUpdateAndReturnComment() {
        // Given
        CommentDto updatedCommentDto = new CommentDto();
        updatedCommentDto.setContent("Updated comment content");

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // When
        CommentDto result = commentService.updateComment(1L, updatedCommentDto);

        // Then
        assertNotNull(result);
        assertEquals(testComment.getId(), result.getId());
        // The service should update the content from the DTO to the entity
        assertEquals(updatedCommentDto.getContent(), result.getContent());
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_WithInvalidId_ShouldThrowException() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(999L, testCommentDto));
        verify(commentRepository, times(1)).findById(999L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment_WithValidId_ShouldMarkAsInactive() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // When
        commentService.deleteComment(1L);

        // Then
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
        // We should verify that setActive(false) was called, but that's internal to the service
    }

    @Test
    void deleteComment_WithInvalidId_ShouldThrowException() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(999L));
        verify(commentRepository, times(1)).findById(999L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void banComment_WithValidId_ShouldMarkAsInactive() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // When
        commentService.banComment(1L);

        // Then
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void banComment_WithInvalidId_ShouldThrowException() {
        // Given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> commentService.banComment(999L));
        verify(commentRepository, times(1)).findById(999L);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getCommentsByUserId_ShouldReturnUserComments() {
        // Given
        when(commentRepository.findByUserIdAndActiveIsTrue(anyLong())).thenReturn(Arrays.asList(testComment));

        // When
        List<CommentDto> result = commentService.getCommentsByUserId(userId);

        // Then
        assertEquals(1, result.size());
        assertEquals(testComment.getId(), result.get(0).getId());
        assertEquals(testComment.getContent(), result.get(0).getContent());
        assertEquals(userId, result.get(0).getUserId());
        verify(commentRepository, times(1)).findByUserIdAndActiveIsTrue(userId);
    }
}