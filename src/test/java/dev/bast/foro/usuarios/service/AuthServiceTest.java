package dev.bast.foro.usuarios.service;

import dev.bast.foro.usuarios.dto.JwtResponse;
import dev.bast.foro.usuarios.dto.LoginRequest;
import dev.bast.foro.usuarios.dto.SignupRequest;
import dev.bast.foro.usuarios.model.Role;
import dev.bast.foro.usuarios.model.Role.ERole;
import dev.bast.foro.usuarios.model.User;
import dev.bast.foro.usuarios.repository.RoleRepository;
import dev.bast.foro.usuarios.repository.UserRepository;
import dev.bast.foro.usuarios.security.jwt.JwtUtils;
import dev.bast.foro.usuarios.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {



    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private Authentication authentication;
    private UserDetailsImpl userDetails;
    private User testUser;
    private Role userRole;

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
        signupRequest.setRoles(Set.of("user"));

        // Setup role
        userRole = new Role();
        userRole.setId(1);
        userRole.setName(ERole.ROLE_USER);

        // Setup user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setActive(true);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        testUser.setRoles(roles);

        // Setup user details
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));
        userDetails = new UserDetailsImpl(
                1L, "testuser", "test@example.com", "encodedPassword", authorities);

        // Setup authentication
        authentication = mock(Authentication.class);
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnJwtResponse() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("jwt.token.string");

        // When
        JwtResponse response = authService.authenticateUser(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt.token.string", response.getToken());
        assertEquals(userDetails.getId(), response.getId());
        assertEquals(userDetails.getUsername(), response.getUsername());
        assertEquals(userDetails.getEmail(), response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_USER"));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
    }

    @Test
    void registerUser_WithValidDataAndNoRoles_ShouldCreateUserWithDefaultRole() {
        // Given
        SignupRequest request = new SignupRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password");
        // No roles specified

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        String result = authService.registerUser(request);

        // Then
        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(encoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findByName(ERole.ROLE_USER);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_WithValidDataAndSpecifiedRoles_ShouldCreateUserWithGivenRoles() {
        // Given
        Role modRole = new Role();
        modRole.setId(2);
        modRole.setName(ERole.ROLE_MODERATOR);
        

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(Optional.of(modRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Set specific roles in the request
        signupRequest.setRoles(Set.of("ROLE_USER", "ROLE_MODERATOR"));

        // When
        String result = authService.registerUser(signupRequest);

        // Then
        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(encoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findByName(ERole.ROLE_USER);
        verify(roleRepository, times(1)).findByName(ERole.ROLE_MODERATOR);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When, Then
        Exception exception = assertThrows(RuntimeException.class, () -> authService.registerUser(signupRequest));
        assertEquals("Error: Username is already taken!", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When, Then
        Exception exception = assertThrows(RuntimeException.class, () -> authService.registerUser(signupRequest));
        assertEquals("Error: Email is already in use!", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithInvalidRole_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Set invalid role
        signupRequest.setRoles(Set.of("invalid_role"));

        // When, Then
        Exception exception = assertThrows(RuntimeException.class, () -> authService.registerUser(signupRequest));
        assertEquals("Error: Invalid role name: invalid_role", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}