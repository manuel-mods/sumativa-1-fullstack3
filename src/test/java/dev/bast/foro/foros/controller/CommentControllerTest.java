package dev.bast.foro.foros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.foro.ForoApplication;
import dev.bast.foro.foros.dto.CommentDto;
import dev.bast.foro.foros.service.CommentService;
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
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired

@MockitoBean
private CommentService mockCommentService;

@MockitoBean
private UserSecurity mockUserSecurity;

    private CommentDto testCommentDto;
    private final Long userId = 1L;
    private final String username = "testuser";
    private final Integer topicId = 1;

    @BeforeEach
    void setUp() {
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
        
        // Make sure to mock the getCommentById call for controller's authorization checks
        when(mockCommentService.getCommentById(anyLong())).thenReturn(testCommentDto);
        
        // Setup security context
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId, username, "email@example.com", "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities));

        // Mock the user security to return the test user
        when(mockUserSecurity.getCurrentUserId()).thenReturn(userId);
        when(mockUserSecurity.getCurrentUsername()).thenReturn(username);
        
    }

    @Test
    void getCommentsByTopicId_ShouldReturnCommentsList() throws Exception {
        // Given
        List<CommentDto> comments = Arrays.asList(testCommentDto);
        when(mockCommentService.getCommentsByTopicId(anyLong())).thenReturn(comments);

        // When/Then
        mockMvc.perform(get("/api/comments/topic/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testCommentDto.getId())))
                .andExpect(jsonPath("$[0].content", is(testCommentDto.getContent())))
                .andExpect(jsonPath("$[0].topicId", is(testCommentDto.getTopicId())));

        verify(mockCommentService, times(1)).getCommentsByTopicId(1L);
    }

    @Test
    void getCommentById_WithValidId_ShouldReturnComment() throws Exception {
        // Given
        when(mockCommentService.getCommentById(anyLong())).thenReturn(testCommentDto);

        // When/Then
        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCommentDto.getId())))
                .andExpect(jsonPath("$.content", is(testCommentDto.getContent())))
                .andExpect(jsonPath("$.topicId", is(testCommentDto.getTopicId())));

        verify(mockCommentService, times(1)).getCommentById(1L);
    }

    @Test
    void getCommentById_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        when(mockCommentService.getCommentById(anyLong())).thenThrow(new ResourceNotFoundException("Comment not found"));

        // When/Then
        mockMvc.perform(get("/api/comments/999"))
                .andExpect(status().isNotFound());

        verify(mockCommentService, times(1)).getCommentById(999L);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createComment_WithValidInput_ShouldReturnCreatedComment() throws Exception {
        // Given
        when(mockCommentService.createComment(any(CommentDto.class), eq(userId), eq(username)))
                .thenReturn(testCommentDto);

        // When/Then
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(testCommentDto.getId())))
                .andExpect(jsonPath("$.content", is(testCommentDto.getContent())))
                .andExpect(jsonPath("$.topicId", is(testCommentDto.getTopicId())));

        verify(mockCommentService, times(1)).createComment(any(CommentDto.class), eq(userId), eq(username));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateComment_WhenUserIsOwner_ShouldUpdateAndReturnComment() throws Exception {
        // Given
        when(mockCommentService.getCommentById(anyLong())).thenReturn(testCommentDto); // El usuario es dueño del comentario
        when(mockCommentService.updateComment(anyLong(), any(CommentDto.class))).thenReturn(testCommentDto);

        // When/Then
        mockMvc.perform(put("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testCommentDto.getId())))
                .andExpect(jsonPath("$.content", is(testCommentDto.getContent())));

        verify(mockCommentService, times(1)).updateComment(anyLong(), any(CommentDto.class));
    }

    @Test
    @WithMockUser(username = "otheruser", roles = "USER")
    void updateComment_WhenUserIsNotOwner_ShouldReturnForbidden() throws Exception {
        // Given
        // Crear un comentario de otro usuario diferente al autenticado
        CommentDto otherUserComment = new CommentDto();
        otherUserComment.setId(1);
        otherUserComment.setContent("This is a test comment");
        otherUserComment.setTopicId(topicId);
        otherUserComment.setUserId(2L); // ID diferente al usuario de prueba
        otherUserComment.setUsername("otherowner"); // Username diferente
        otherUserComment.setCreatedAt(LocalDateTime.now());
        otherUserComment.setUpdatedAt(LocalDateTime.now());
        otherUserComment.setActive(true);
        
        when(mockCommentService.getCommentById(anyLong())).thenReturn(otherUserComment);

        // When/Then
        mockMvc.perform(put("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDto)))
                .andExpect(status().isForbidden());

        verify(mockCommentService, never()).updateComment(anyLong(), any(CommentDto.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteComment_WhenUserIsOwner_ShouldReturnNoContent() throws Exception {
        // Given
        when(mockCommentService.getCommentById(anyLong())).thenReturn(testCommentDto); // El usuario es dueño del comentario
        doNothing().when(mockCommentService).deleteComment(anyLong());

        // When/Then
        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());

        verify(mockCommentService, times(1)).deleteComment(1L);
    }

    @Test
    @WithMockUser(username = "otheruser", roles = "USER")
    void deleteComment_WhenUserIsNotOwner_ShouldReturnForbidden() throws Exception {
        // Given
        // Crear un comentario de otro usuario diferente al autenticado
        CommentDto otherUserComment = new CommentDto();
        otherUserComment.setId(1);
        otherUserComment.setContent("This is a test comment");
        otherUserComment.setTopicId(topicId);
        otherUserComment.setUserId(2L); // ID diferente al usuario de prueba
        otherUserComment.setUsername("otherowner"); // Username diferente
        otherUserComment.setCreatedAt(LocalDateTime.now());
        otherUserComment.setUpdatedAt(LocalDateTime.now());
        otherUserComment.setActive(true);
        
        when(mockCommentService.getCommentById(anyLong())).thenReturn(otherUserComment);

        // When/Then
        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isForbidden());

        verify(mockCommentService, never()).deleteComment(anyLong());
    }

    @Test
    @WithMockUser(username = "moderator", roles = "MODERATOR")
    void banComment_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId, username, "email@example.com", "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities));

        doNothing().when(mockCommentService).banComment(anyLong());

        // When/Then
        mockMvc.perform(put("/api/comments/1/ban"))
                .andExpect(status().isNoContent());

        verify(mockCommentService, times(1)).banComment(1L);
    }

    @Test
    void getCommentsByUserId_ShouldReturnUserComments() throws Exception {
        // Given
        List<CommentDto> comments = Arrays.asList(testCommentDto);
        when(mockCommentService.getCommentsByUserId(anyLong())).thenReturn(comments);

        // When/Then
        mockMvc.perform(get("/api/comments/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testCommentDto.getId())))
                .andExpect(jsonPath("$[0].content", is(testCommentDto.getContent())))
                .andExpect(jsonPath("$[0].userId", is(userId.intValue())));

        verify(mockCommentService, times(1)).getCommentsByUserId(1L);
    }
}