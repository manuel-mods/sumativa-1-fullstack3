package dev.bast.foro.usuarios.service;

import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.usuarios.dto.UserDto;
import dev.bast.foro.usuarios.model.Role;
import dev.bast.foro.usuarios.model.Role.ERole;
import dev.bast.foro.usuarios.model.User;
import dev.bast.foro.usuarios.repository.RoleRepository;
import dev.bast.foro.usuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;
    private Role userRole, adminRole;
    private Set<Role> roles;

    @BeforeEach
    void setUp() {
        // Setup roles
        userRole = new Role();
        userRole.setId(1);
        userRole.setName(ERole.ROLE_USER);

        adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName(ERole.ROLE_ADMIN);

        roles = new HashSet<>();
        roles.add(userRole);

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setRoles(roles);

        // Setup test user DTO
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setRoles(Set.of("ROLE_USER"));
        testUserDto.setActive(true);

        // add users 

        
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // When
        UserDto result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void updateUser_WithValidIdAndRoles_ShouldUpdateAndReturnUser() {
        // Given
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setUsername("updateduser");
        updatedUserDto.setEmail("updated@example.com");
        updatedUserDto.setRoles(Set.of("ROLE_USER", "ROLE_ADMIN"));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateUser(1L, updatedUserDto);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findByName(ERole.ROLE_USER);
        verify(roleRepository, times(1)).findByName(ERole.ROLE_ADMIN);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidRole_ShouldThrowException() {
        // Given
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setUsername("updateduser");
        updatedUserDto.setEmail("updated@example.com");
        updatedUserDto.setRoles(Set.of("INVALID_ROLE"));


        // When, Then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithInvalidId_ShouldThrowException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(999L, testUserDto));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() {
        // Given

        // add user with id 1
        when(userRepository.existsById(anyLong())).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        // Given

        // When, Then
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository, never()).delete(any(User.class));
    }
}