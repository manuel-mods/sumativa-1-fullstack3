package dev.bast.foro.foros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.foro.ForoApplication;
import dev.bast.foro.foros.dto.TopicDto;
import dev.bast.foro.foros.service.TopicService;
import dev.bast.foro.usuarios.security.UserSecurity;
import dev.bast.foro.usuarios.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ForoApplication.class)
@AutoConfigureMockMvc
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TopicService topicService;

    @MockitoBean
    private UserSecurity userSecurity;

    private TopicDto testTopicDto;
    private final Long userId = 1L;
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
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
        
        // Setup security context
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId, username, "email@example.com", "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
    }

    @Test
    void getAllTopics_ShouldReturnTopicsList() throws Exception {
        // Given
        List<TopicDto> topics = Arrays.asList(testTopicDto);
        when(topicService.getAllTopics()).thenReturn(topics);

        // When/Then
        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testTopicDto.getId())))
                .andExpect(jsonPath("$[0].title", is(testTopicDto.getTitle())))
                .andExpect(jsonPath("$[0].content", is(testTopicDto.getContent())));

        verify(topicService, times(1)).getAllTopics();
    }

    @Test
    void getTopicById_WithValidId_ShouldReturnTopic() throws Exception {
        // Given
        when(topicService.getTopicById(anyLong())).thenReturn(testTopicDto);

        // When/Then
        mockMvc.perform(get("/api/topics/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testTopicDto.getId())))
                .andExpect(jsonPath("$.title", is(testTopicDto.getTitle())))
                .andExpect(jsonPath("$.content", is(testTopicDto.getContent())));

        verify(topicService, times(1)).getTopicById(1L);
    }

    @Test
    void getTopicById_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        when(topicService.getTopicById(anyLong())).thenThrow(new ResourceNotFoundException("Topic not found"));

        // When/Then
        mockMvc.perform(get("/api/topics/999"))
                .andExpect(status().isNotFound());

        verify(topicService, times(1)).getTopicById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTopic_WithValidInput_ShouldReturnCreatedTopic() throws Exception {
        // Given
        when(topicService.createTopic(any(TopicDto.class), anyLong(), anyString()))
                .thenReturn(testTopicDto);

        // When/Then
        mockMvc.perform(post("/api/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTopicDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(testTopicDto.getId())))
                .andExpect(jsonPath("$.title", is(testTopicDto.getTitle())))
                .andExpect(jsonPath("$.content", is(testTopicDto.getContent())));

        verify(topicService, times(1)).createTopic(any(TopicDto.class), anyLong(), anyString());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateTopic_WhenUserIsOwner_ShouldUpdateAndReturnTopic() throws Exception {
        // Given
        when(topicService.getTopicById(anyLong())).thenReturn(testTopicDto); // El usuario es dueño del tema
        when(topicService.updateTopic(anyLong(), any(TopicDto.class))).thenReturn(testTopicDto);

        // When/Then
        mockMvc.perform(put("/api/topics/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTopicDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testTopicDto.getId())))
                .andExpect(jsonPath("$.title", is(testTopicDto.getTitle())))
                .andExpect(jsonPath("$.content", is(testTopicDto.getContent())));

        verify(topicService, times(1)).updateTopic(anyLong(), any(TopicDto.class));
    }

    @Test
    @WithMockUser(username = "otheruser", roles = "USER")
    void updateTopic_WhenUserIsNotOwner_ShouldReturnForbidden() throws Exception {
        // Given
        // Crear un tema de otro usuario diferente al autenticado
        TopicDto otherUserTopic = new TopicDto();
        otherUserTopic.setId(1);
        otherUserTopic.setTitle("Test Topic");
        otherUserTopic.setContent("This is a test topic");
        otherUserTopic.setUserId(2L); // ID diferente al usuario de prueba
        otherUserTopic.setUsername("otherowner"); // Username diferente
        otherUserTopic.setCreatedAt(LocalDateTime.now());
        otherUserTopic.setUpdatedAt(LocalDateTime.now());
        otherUserTopic.setActive(true);
        
        when(topicService.getTopicById(anyLong())).thenReturn(otherUserTopic);

        // When/Then
        mockMvc.perform(put("/api/topics/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTopicDto)))
                .andExpect(status().isForbidden());

        verify(topicService, never()).updateTopic(anyLong(), any(TopicDto.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteTopic_WhenUserIsOwner_ShouldReturnNoContent() throws Exception {
        // Given
        when(topicService.getTopicById(anyLong())).thenReturn(testTopicDto); // El usuario es dueño del tema
        doNothing().when(topicService).deleteTopic(anyLong());

        // When/Then
        mockMvc.perform(delete("/api/topics/1"))
                .andExpect(status().isNoContent());

        verify(topicService, times(1)).deleteTopic(1L);
    }

    @Test
    @WithMockUser(username = "otheruser", roles = "USER")
    void deleteTopic_WhenUserIsNotOwner_ShouldReturnForbidden() throws Exception {
        // Given
        // Crear un tema de otro usuario diferente al autenticado
        TopicDto otherUserTopic = new TopicDto();
        otherUserTopic.setId(1);
        otherUserTopic.setTitle("Test Topic");
        otherUserTopic.setContent("This is a test topic");
        otherUserTopic.setUserId(2L); // ID diferente al usuario de prueba
        otherUserTopic.setUsername("otherowner"); // Username diferente
        otherUserTopic.setCreatedAt(LocalDateTime.now());
        otherUserTopic.setUpdatedAt(LocalDateTime.now());
        otherUserTopic.setActive(true);
        
        when(topicService.getTopicById(anyLong())).thenReturn(otherUserTopic);

        // When/Then
        mockMvc.perform(delete("/api/topics/1"))
                .andExpect(status().isForbidden());

        verify(topicService, never()).deleteTopic(anyLong());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void banTopic_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_MODERATOR"));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId, username, "email@example.com", "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
                
        doNothing().when(topicService).banTopic(anyLong());

        // When/Then
        mockMvc.perform(put("/api/topics/1/ban"))
                .andExpect(status().isNoContent());

        verify(topicService, times(1)).banTopic(1L);
    }

    @Test
    void getTopicsByUserId_ShouldReturnUserTopics() throws Exception {
        // Given
        List<TopicDto> topics = Arrays.asList(testTopicDto);
        when(topicService.getTopicsByUserId(anyLong())).thenReturn(topics);

        // When/Then
        mockMvc.perform(get("/api/topics/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testTopicDto.getId())))
                .andExpect(jsonPath("$[0].title", is(testTopicDto.getTitle())))
                .andExpect(jsonPath("$[0].userId", is(userId.intValue())));

        verify(topicService, times(1)).getTopicsByUserId(1L);
    }
}