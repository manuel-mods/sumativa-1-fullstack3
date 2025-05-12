package dev.bast.foro.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.usuarios.dto.UserDto;
import dev.bast.foro.usuarios.security.UserSecurity;
import dev.bast.foro.usuarios.security.services.UserDetailsImpl;
import dev.bast.foro.usuarios.service.UserService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = dev.bast.foro.foro.ForoApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserSecurity userSecurity;

    private UserDto testUserDto;
    private final Long userId = 1L;
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        // Setup test user DTO
        testUserDto = new UserDto();
        testUserDto.setId(userId);
        testUserDto.setUsername(username);
        testUserDto.setEmail("test@example.com");
        testUserDto.setRoles(Set.of("ROLE_USER"));
        testUserDto.setActive(true);
        
        // Setup security context
        // List<SimpleGrantedAuthority> authorities = Collections.singletonList(
        //         new SimpleGrantedAuthority("ROLE_USER"));
        // UserDetailsImpl userDetails = new UserDetailsImpl(
        //         userId, username, "test@example.com", "password", authorities);
        // SecurityContextHolder.getContext().setAuthentication(
        //         new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void getAllUsers_WhenAdmin_ShouldReturnUsersList() throws Exception {
        // Given
        List<UserDto> users = Arrays.asList(testUserDto);
        when(userService.getAllUsers()).thenReturn(users);

        // When/Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userId.intValue())))
                .andExpect(jsonPath("$[0].username", is(username)))
                .andExpect(jsonPath("$[0].email", is("test@example.com")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void getAllUsers_WhenNotAdmin_ShouldReturnForbidden() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void getUserById_WhenAdmin_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserById(anyLong())).thenReturn(testUserDto);

        // When/Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_ADMIN"})
    void getUserById_WhenCurrentUser_ShouldReturnUser() throws Exception {
        // Given

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId, username, "test@example.com", "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
        
        when(userSecurity.isCurrentUser(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(testUserDto);

        // When/Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void getUserById_WhenNotCurrentUserOrAdmin_ShouldReturnForbidden() throws Exception {
        // Given
        when(userSecurity.isCurrentUser(anyLong())).thenReturn(false);

        // When/Then
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isForbidden());

        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_ADMIN"})
    void getUserById_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        when(userSecurity.isCurrentUser(anyLong())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenThrow(new ResourceNotFoundException("User not found"));

        // When/Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void updateUser_WhenAdmin_ShouldUpdateAndReturnUser() throws Exception {
        // Given
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setUsername("updateduser");
        updatedUserDto.setEmail("updated@example.com");
        updatedUserDto.setRoles(Set.of("ROLE_USER", "ROLE_ADMIN"));

        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(testUserDto);

        // When/Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.username", is(username)));

        verify(userService, times(1)).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_ADMIN"})
    void updateUser_WhenCurrentUser_ShouldUpdateAndReturnUser() throws Exception {
        // Given
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setUsername("updateduser");
        updatedUserDto.setEmail("updated@example.com");

        when(userSecurity.isCurrentUser(anyLong())).thenReturn(true);
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(testUserDto);

        // When/Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.username", is(username)));

        verify(userService, times(1)).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void updateUser_WhenNotCurrentUserOrAdmin_ShouldReturnForbidden() throws Exception {
        // Given
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setUsername("updateduser");
        updatedUserDto.setEmail("updated@example.com");

        when(userSecurity.isCurrentUser(anyLong())).thenReturn(false);

        // When/Then
        mockMvc.perform(put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserDto)))
                .andExpect(status().isForbidden());

        
        verify(userService, never()).updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void deleteUser_WhenAdmin_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(anyLong());

        // When/Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_ADMIN"})
    void deleteUser_WhenCurrentUser_ShouldReturnNoContent() throws Exception {
        // Given
        when(userSecurity.isCurrentUser(anyLong())).thenReturn(true);
        doNothing().when(userService).deleteUser(anyLong());

        // When/Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void deleteUser_WhenNotCurrentUserOrAdmin_ShouldReturnForbidden() throws Exception {
        // Given
        when(userSecurity.isCurrentUser(anyLong())).thenReturn(false);

        // When/Then
        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(anyLong());
    }
}