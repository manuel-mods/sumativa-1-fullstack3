package dev.bast.foro.usuarios.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        dto.setRoles(roles);
        dto.setActive(true);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        UserDto dto = new UserDto();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getRoles());
        assertFalse(dto.isActive());
    }

    @Test
    void testAllArgsConstructor() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        UserDto dto = new UserDto(1L, "testuser", "test@example.com", roles, true);
        
        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals(roles, dto.getRoles());
        assertTrue(dto.isActive());
    }

    @Test
    void testBlankUsernameValidation() {
        UserDto dto = new UserDto();
        dto.setUsername("");
        dto.setEmail("test@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(2, violations.size());
        
        boolean hasNotBlankViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Username is required"));
        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Username must be between 3 and 50 characters"));
        
        assertTrue(hasNotBlankViolation);
        assertTrue(hasSizeViolation);
    }

    @Test
    void testNullUsernameValidation() {
        UserDto dto = new UserDto();
        dto.setUsername(null);
        dto.setEmail("test@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooShortValidation() {
        UserDto dto = new UserDto();
        dto.setUsername("ab");
        dto.setEmail("test@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username must be between 3 and 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooLongValidation() {
        UserDto dto = new UserDto();
        String longUsername = "a".repeat(51);
        dto.setUsername(longUsername);
        dto.setEmail("test@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username must be between 3 and 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankEmailValidation() {
        UserDto dto = new UserDto();
        dto.setUsername("validuser");
        dto.setEmail("");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        
        boolean hasNotBlankViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email is required"));
        
        assertTrue(hasNotBlankViolation);
    
    }

    @Test
    void testNullEmailValidation() {
        UserDto dto = new UserDto();
        dto.setUsername("validuser");
        dto.setEmail(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Email is required", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmailFormatValidation() {
        UserDto dto = new UserDto();
        dto.setUsername("validuser");
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmailWithoutDomainValidation() {
        UserDto dto = new UserDto();
        dto.setUsername("validuser");
        dto.setEmail("user@");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmailWithoutAtValidation() {
        UserDto dto = new UserDto();
        dto.setUsername("validuser");
        dto.setEmail("userdomain.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void testValidEmailFormats() {
        UserDto dto = new UserDto();
        dto.setUsername("validuser");
        
        String[] validEmails = {
            "user@domain.com",
            "user.name@domain.com",
            "user+tag@domain.co.uk",
            "user123@sub.domain.com"
        };
        
        for (String email : validEmails) {
            dto.setEmail(email);
            Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
        }
    }

    @Test
    void testUsernameExactlyMinLength() {
        UserDto dto = new UserDto();
        dto.setUsername("abc");
        dto.setEmail("test@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testUsernameExactlyMaxLength() {
        UserDto dto = new UserDto();
        dto.setUsername("a".repeat(50));
        dto.setEmail("test@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        UserDto dto = new UserDto();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        
        dto.setId(1L);
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setRoles(roles);
        dto.setActive(true);
        
        assertEquals(1L, dto.getId());
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals(roles, dto.getRoles());
        assertTrue(dto.isActive());
    }

    @Test
    void testRolesManipulation() {
        UserDto dto = new UserDto();
        Set<String> roles = new HashSet<>();
        
        dto.setRoles(roles);
        assertTrue(dto.getRoles().isEmpty());
        
        roles.add("ROLE_USER");
        dto.setRoles(roles);
        assertEquals(1, dto.getRoles().size());
        assertTrue(dto.getRoles().contains("ROLE_USER"));
        
        roles.add("ROLE_ADMIN");
        dto.setRoles(roles);
        assertEquals(2, dto.getRoles().size());
        assertTrue(dto.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    void testNullRoles() {
        UserDto dto = new UserDto();
        dto.setUsername("validuser");
        dto.setEmail("test@example.com");
        dto.setRoles(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
        assertNull(dto.getRoles());
    }

    @Test
    void testEqualsAndHashCode() {
        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setUsername("testuser");
        dto1.setEmail("test@example.com");
        
        UserDto dto2 = new UserDto();
        dto2.setId(1L);
        dto2.setUsername("testuser");
        dto2.setEmail("test@example.com");
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testNotEquals() {
        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setUsername("testuser1");
        
        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setUsername("testuser2");
        
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testToString() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("UserDto"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("email=test@example.com"));
    }

    @Test
    void testActiveFlag() {
        UserDto dto = new UserDto();
        
        // Default value
        assertFalse(dto.isActive());
        
        // Set to true
        dto.setActive(true);
        assertTrue(dto.isActive());
        
        // Set to false
        dto.setActive(false);
        assertFalse(dto.isActive());
    }
}
