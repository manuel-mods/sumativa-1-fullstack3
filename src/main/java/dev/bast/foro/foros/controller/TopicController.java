package dev.bast.foro.foros.controller;

import dev.bast.foro.foros.dto.TopicDto;
import dev.bast.foro.foros.service.TopicService;
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
@RequestMapping("/api/topics")
public class TopicController {
    
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        List<TopicDto> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicDto> getTopicById(@PathVariable Long id) {
        TopicDto topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TopicDto> createTopic(@Valid @RequestBody TopicDto topicDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        TopicDto createdTopic = topicService.createTopic(
                topicDto, 
                userDetails.getId(), 
                userDetails.getUsername()
        );
        
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TopicDto> updateTopic(@PathVariable Long id, @Valid @RequestBody TopicDto topicDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        TopicDto existingTopic = topicService.getTopicById(id);
        
        // Only allow the creator or admin/moderator to update
        if (!existingTopic.getUserId().equals(userDetails.getId()) && 
                !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                       a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        TopicDto updatedTopic = topicService.updateTopic(id, topicDto);
        return ResponseEntity.ok(updatedTopic);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        TopicDto existingTopic = topicService.getTopicById(id);
        
        // Only allow the creator or admin/moderator to delete
        if (!existingTopic.getUserId().equals(userDetails.getId()) && 
                !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                       a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> banTopic(@PathVariable Long id) {
        topicService.banTopic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TopicDto>> getTopicsByUserId(@PathVariable Long userId) {
        List<TopicDto> topics = topicService.getTopicsByUserId(userId);
        return ResponseEntity.ok(topics);
    }
}