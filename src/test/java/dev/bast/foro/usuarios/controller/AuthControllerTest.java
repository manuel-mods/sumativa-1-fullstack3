package dev.bast.foro.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bast.foro.usuarios.dto.JwtResponse;
import dev.bast.foro.usuarios.dto.LoginRequest;
import dev.bast.foro.usuarios.dto.SignupRequest;
import dev.bast.foro.usuarios.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = dev.bast.foro.foro.ForoApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private JwtResponse jwtResponse;

    @BeforeEach
    void setUp() {
        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        // Setup signup request
        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password");
        Set<String> roles = new HashSet<>();
        roles.add("user");
        signupRequest.setRoles(roles);

        // Setup JWT response
        Set<String> responseRoles = new HashSet<>();
        responseRoles.add("ROLE_USER");
        jwtResponse = new JwtResponse("jwt.token.string", 1L, "testuser", "test@example.com", responseRoles);
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnJwtResponse() throws Exception {
        // Given
        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(jwtResponse);

        // When/Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", is("jwt.token.string")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(authService, times(1)).authenticateUser(any(LoginRequest.class));
    }

    @Test
    void registerUser_WithValidData_ShouldReturnSuccessMessage() throws Exception {
        // Given
        when(authService.registerUser(any(SignupRequest.class))).thenReturn("User registered successfully!");

        // When/Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        verify(authService, times(1)).registerUser(any(SignupRequest.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldReturnBadRequest() throws Exception {
        // Given
        when(authService.registerUser(any(SignupRequest.class)))
                .thenThrow(new RuntimeException("Error: Username is already taken!"));

        // When/Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Error: Username is already taken!")));

        verify(authService, times(1)).registerUser(any(SignupRequest.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        when(authService.registerUser(any(SignupRequest.class)))
                .thenThrow(new RuntimeException("Error: Email is already in use!"));

        // When/Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Error: Email is already in use!")));

        verify(authService, times(1)).registerUser(any(SignupRequest.class));
    }

    @Test
    void registerUser_WithInvalidRole_ShouldReturnBadRequest() throws Exception {
        // Given
        when(authService.registerUser(any(SignupRequest.class)))
                .thenThrow(new RuntimeException("Error: Role is not found."));

        // When/Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Error: Role is not found.")));

        verify(authService, times(1)).registerUser(any(SignupRequest.class));
    }
}