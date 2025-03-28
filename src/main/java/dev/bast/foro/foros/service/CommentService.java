package dev.bast.foro.foros.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.foros.dto.CommentDto;
import dev.bast.foro.foros.model.Comment;
import dev.bast.foro.foros.repository.CommentRepository;
import dev.bast.foro.foros.repository.TopicRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;

    public CommentService(CommentRepository commentRepository, TopicRepository topicRepository) {
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
    }

    public List<CommentDto> getCommentsByTopicId(Long topicId) {
        log.info("Retrieving comments for topic id: {}", topicId);
        
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("Topic not found with id: " + topicId);
        }
        
        return commentRepository.findByTopicIdAndActiveIsTrue(topicId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CommentDto getCommentById(Long id) {
        log.info("Retrieving comment with id: {}", id);
        
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        
        return convertToDto(comment);
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId, String username) {
        log.info("Creating new comment by user: {} for topic: {}", username, commentDto.getTopicId());
        
        if (!topicRepository.existsById(commentDto.getTopicId())) {
            throw new ResourceNotFoundException("Topic not found with id: " + commentDto.getTopicId());
        }
        
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setTopicId(commentDto.getTopicId());
        comment.setUserId(userId);
        comment.setUsername(username);
        comment.setActive(true);
        
        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(Long id, CommentDto commentDto) {
        log.info("Updating comment with id: {}", id);
        
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        
        comment.setContent(commentDto.getContent());
        
        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long id) {
        log.info("Deleting comment with id: {}", id);
        
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        
        comment.setActive(false);
        commentRepository.save(comment);
    }

    @Transactional
    public void banComment(Long id) {
        log.info("Banning comment with id: {}", id);
        
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        
        comment.setActive(false);
        commentRepository.save(comment);
    }

    public List<CommentDto> getCommentsByUserId(Long userId) {
        log.info("Retrieving comments for user id: {}", userId);
        
        return commentRepository.findByUserIdAndActiveIsTrue(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setTopicId(comment.getTopicId());
        dto.setUserId(comment.getUserId());
        dto.setUsername(comment.getUsername());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setActive(comment.isActive());
        return dto;
    }
}