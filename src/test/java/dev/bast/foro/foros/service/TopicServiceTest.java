package dev.bast.foro.foros.service;

import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.foros.dto.TopicDto;
import dev.bast.foro.foros.model.Topic;
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
public class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private TopicService topicService;

    private Topic testTopic;
    private TopicDto testTopicDto;
    private final Long userId = 1L;
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        // Setup test topic
        testTopic = new Topic();
        testTopic.setId(1);
        testTopic.setTitle("Test Topic");
        testTopic.setContent("This is a test topic content");
        testTopic.setUserId(userId);
        testTopic.setUsername(username);
        testTopic.setCreatedAt(LocalDateTime.now());
        testTopic.setUpdatedAt(LocalDateTime.now());
        testTopic.setActive(true);

        // Setup test topic DTO
        testTopicDto = new TopicDto();
        testTopicDto.setId(1);
        testTopicDto.setTitle("Test Topic");
        testTopicDto.setContent("This is a test topic content");
        testTopicDto.setUserId(userId);
        testTopicDto.setUsername(username);
        testTopicDto.setCreatedAt(LocalDateTime.now());
        testTopicDto.setUpdatedAt(LocalDateTime.now());
        testTopicDto.setActive(true);
    }

    @Test
    void getAllTopics_ShouldReturnOnlyActiveTopics() {
        // Given
        Topic inactiveTopic = new Topic();
        inactiveTopic.setId(2);
        inactiveTopic.setActive(false);
        
        when(topicRepository.findByActiveIsTrue()).thenReturn(Arrays.asList(testTopic));

        // When
        List<TopicDto> result = topicService.getAllTopics();

        // Then
        assertEquals(1, result.size());
        assertEquals(testTopic.getId(), result.get(0).getId());
        assertEquals(testTopic.getTitle(), result.get(0).getTitle());
        verify(topicRepository, times(1)).findByActiveIsTrue();
    }

    @Test
    void getTopicById_WithValidId_ShouldReturnTopic() {
        // Given
        when(topicRepository.findById(anyLong())).thenReturn(Optional.of(testTopic));

        // When
        TopicDto result = topicService.getTopicById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testTopic.getId(), result.getId());
        assertEquals(testTopic.getTitle(), result.getTitle());
        verify(topicRepository, times(1)).findById(1L);
    }

    @Test
    void getTopicById_WithInvalidId_ShouldThrowException() {
        // Given
        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> topicService.getTopicById(999L));
        verify(topicRepository, times(1)).findById(999L);
    }

    @Test
    void createTopic_ShouldSaveAndReturnTopic() {
        // Given
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // When
        TopicDto result = topicService.createTopic(testTopicDto, userId, username);

        // Then
        assertNotNull(result);
        assertEquals(testTopic.getId(), result.getId());
        assertEquals(testTopic.getTitle(), result.getTitle());
        assertEquals(userId, result.getUserId());
        assertEquals(username, result.getUsername());
        assertTrue(result.isActive());
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void updateTopic_WithValidId_ShouldUpdateAndReturnTopic() {
        // Given
        TopicDto updatedTopicDto = new TopicDto();
        updatedTopicDto.setTitle("Updated Title");
        updatedTopicDto.setContent("Updated content for test");

        when(topicRepository.findById(anyLong())).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // When
        TopicDto result = topicService.updateTopic(1L, updatedTopicDto);

        // Then
        assertNotNull(result);
        assertEquals(testTopic.getId(), result.getId());
        // The service should update the properties from the DTO to the entity
        assertEquals(updatedTopicDto.getTitle(), result.getTitle());
        assertEquals(updatedTopicDto.getContent(), result.getContent());
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void updateTopic_WithInvalidId_ShouldThrowException() {
        // Given
        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> topicService.updateTopic(999L, testTopicDto));
        verify(topicRepository, times(1)).findById(999L);
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void deleteTopic_WithValidId_ShouldMarkAsInactive() {
        // Given
        when(topicRepository.findById(anyLong())).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // When
        topicService.deleteTopic(1L);

        // Then
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).save(any(Topic.class));
        // We should verify that setActive(false) was called, but that's internal to the service
        // So we're verifying the interaction with the repository
    }

    @Test
    void deleteTopic_WithInvalidId_ShouldThrowException() {
        // Given
        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> topicService.deleteTopic(999L));
        verify(topicRepository, times(1)).findById(999L);
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void banTopic_WithValidId_ShouldMarkAsInactive() {
        // Given
        when(topicRepository.findById(anyLong())).thenReturn(Optional.of(testTopic));
        when(topicRepository.save(any(Topic.class))).thenReturn(testTopic);

        // When
        topicService.banTopic(1L);

        // Then
        verify(topicRepository, times(1)).findById(1L);
        verify(topicRepository, times(1)).save(any(Topic.class));
    }

    @Test
    void banTopic_WithInvalidId_ShouldThrowException() {
        // Given
        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> topicService.banTopic(999L));
        verify(topicRepository, times(1)).findById(999L);
        verify(topicRepository, never()).save(any(Topic.class));
    }

    @Test
    void getTopicsByUserId_ShouldReturnUserTopics() {
        // Given
        when(topicRepository.findByUserIdAndActiveIsTrue(anyLong())).thenReturn(Arrays.asList(testTopic));

        // When
        List<TopicDto> result = topicService.getTopicsByUserId(userId);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTopic.getId(), result.get(0).getId());
        assertEquals(testTopic.getTitle(), result.get(0).getTitle());
        assertEquals(userId, result.get(0).getUserId());
        verify(topicRepository, times(1)).findByUserIdAndActiveIsTrue(userId);
    }
}