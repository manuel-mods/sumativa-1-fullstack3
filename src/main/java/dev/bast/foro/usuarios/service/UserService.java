package dev.bast.foro.usuarios.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.bast.foro.common.exception.ResourceNotFoundException;
import dev.bast.foro.usuarios.dto.UserDto;
import dev.bast.foro.usuarios.model.Role;
import dev.bast.foro.usuarios.model.User;
import dev.bast.foro.usuarios.repository.RoleRepository;
import dev.bast.foro.usuarios.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserDto> getAllUsers() {
        log.info("Retrieving all users");
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        log.info("Retrieving user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setActive(userDto.isActive());
        
        // Update roles if provided
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            userDto.getRoles().forEach(roleName -> {
                try {
                    Role.ERole eRole = Role.ERole.valueOf(roleName);
                    Role role = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid role name: {}", roleName);
                }
            });
            if (!roles.isEmpty()) {
                user.setRoles(roles);
            }
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        dto.setRoles(roles);
        
        return dto;
    }
}