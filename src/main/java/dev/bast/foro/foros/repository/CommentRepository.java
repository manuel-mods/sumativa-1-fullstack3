package dev.bast.foro.foros.repository;

import dev.bast.foro.foros.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByTopicIdAndActiveIsTrue(Long topicId);
    
    List<Comment> findByUserIdAndActiveIsTrue(Long userId);
    
    List<Comment> findByTopicId(Long topicId);
}