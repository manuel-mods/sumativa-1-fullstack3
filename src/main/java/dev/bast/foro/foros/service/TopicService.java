package dev.bast.foro.foros.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.foros.dto.TopicDto;
import dev.bast.foro.foros.model.Topic;
import dev.bast.foro.foros.repository.TopicRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TopicService {
    
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<TopicDto> getAllTopics() {
        log.info("Retrieving all active topics");
        return topicRepository.findByActiveIsTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TopicDto getTopicById(Long id) {
        log.info("Retrieving topic with id: {}", id);
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        return convertToDto(topic);
    }

    @Transactional
    public TopicDto createTopic(TopicDto topicDto, Long userId, String username) {
        log.info("Creating new topic by user: {}", username);
        
        Topic topic = new Topic();
        topic.setTitle(topicDto.getTitle());
        topic.setContent(topicDto.getContent());
        topic.setUserId(userId);
        topic.setUsername(username);
        topic.setActive(true);
        
        Topic savedTopic = topicRepository.save(topic);
        return convertToDto(savedTopic);
    }

    @Transactional
    public TopicDto updateTopic(Long id, TopicDto topicDto) {
        log.info("Updating topic with id: {}", id);
        
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        
        topic.setTitle(topicDto.getTitle());
        topic.setContent(topicDto.getContent());
        
        Topic updatedTopic = topicRepository.save(topic);
        return convertToDto(updatedTopic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        log.info("Deleting topic with id: {}", id);
        
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        
        topic.setActive(false);
        topicRepository.save(topic);
    }

    @Transactional
    public void banTopic(Long id) {
        log.info("Banning topic with id: {}", id);
        
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        
        topic.setActive(false);
        topicRepository.save(topic);
    }

    public List<TopicDto> getTopicsByUserId(Long userId) {
        log.info("Retrieving topics for user id: {}", userId);
        
        return topicRepository.findByUserIdAndActiveIsTrue(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TopicDto convertToDto(Topic topic) {
        TopicDto dto = new TopicDto();
        dto.setId(topic.getId());
        dto.setTitle(topic.getTitle());
        dto.setContent(topic.getContent());
        dto.setUserId(topic.getUserId());
        dto.setUsername(topic.getUsername());
        dto.setCreatedAt(topic.getCreatedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        dto.setActive(topic.isActive());
        return dto;
    }
}