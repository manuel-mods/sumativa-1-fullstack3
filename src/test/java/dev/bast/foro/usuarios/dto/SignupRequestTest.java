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

class SignupRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        request.setRoles(roles);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        SignupRequest request = new SignupRequest();
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertNull(request.getRoles());
    }

    @Test
    void testAllArgsConstructor() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        
        SignupRequest request = new SignupRequest("testuser", "test@example.com", "password123", roles);
        
        assertEquals("testuser", request.getUsername());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals(roles, request.getRoles());
    }

    @Test
    void testBlankUsernameValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
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
        SignupRequest request = new SignupRequest();
        request.setUsername(null);
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooShortValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("ab");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Username must be between 3 and 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooLongValidation() {
        SignupRequest request = new SignupRequest();
        String longUsername = "a".repeat(51);
        request.setUsername(longUsername);
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Username must be between 3 and 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankEmailValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        
        boolean hasNotBlankViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email is required"));
        boolean hasEmailViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email should be valid"));
        
        assertTrue(hasNotBlankViolation);
        assertFalse(hasEmailViolation);
    }

    @Test
    void testNullEmailValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail(null);
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Email is required", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmailFormatValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("invalid-email");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankPasswordValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
        
        boolean hasNotBlankViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password is required"));
        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password must be between 6 and 40 characters"));
        
        assertTrue(hasNotBlankViolation);
        assertTrue(hasSizeViolation);
    }

    @Test
    void testNullPasswordValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword(null);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    void testPasswordTooShortValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("12345");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Password must be between 6 and 40 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testPasswordTooLongValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        String longPassword = "a".repeat(41);
        request.setPassword(longPassword);

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Password must be between 6 and 40 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testValidEmailFormats() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        
        String[] validEmails = {
            "user@domain.com",
            "user.name@domain.com",
            "user+tag@domain.co.uk",
            "user123@sub.domain.com"
        };
        
        for (String email : validEmails) {
            request.setEmail(email);
            Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
        }
    }

    // @Test
    // void testInvalidEmailFormats() {
    //     SignupRequest request = new SignupRequest();
    //     request.setUsername("testuser");
    //     request.setPassword("password123");
        
    //     String[] invalidEmails = {
    //         "invalid",
    //         "user@",
    //         "@domain.com",
    //         "user@domain",
    //         "user domain.com"
    //     };
        
    //     for (String email : invalidEmails) {
    //         request.setEmail(email);
    //         Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
    //         assertEquals(0, violations.size(), "Email should be invalid: " + email);
    //         assertEquals("Email should be valid", violations.iterator().next().getMessage());
    //     }
    // }

    @Test
    void testUsernameExactlyMinLength() {
        SignupRequest request = new SignupRequest();
        request.setUsername("abc");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testUsernameExactlyMaxLength() {
        SignupRequest request = new SignupRequest();
        request.setUsername("a".repeat(50));
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testPasswordExactlyMinLength() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("123456");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testPasswordExactlyMaxLength() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("a".repeat(40));

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testRolesManipulation() {
        SignupRequest request = new SignupRequest();
        
        assertNull(request.getRoles());
        
        Set<String> roles = new HashSet<>();
        request.setRoles(roles);
        assertTrue(request.getRoles().isEmpty());
        
        roles.add("ROLE_USER");
        request.setRoles(roles);
        assertEquals(1, request.getRoles().size());
        assertTrue(request.getRoles().contains("ROLE_USER"));
        
        roles.add("ROLE_ADMIN");
        request.setRoles(roles);
        assertEquals(2, request.getRoles().size());
        assertTrue(request.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    void testValidWithoutRoles() {
        SignupRequest request = new SignupRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        // roles is null

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        SignupRequest request = new SignupRequest();
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        
        request.setUsername("user123");
        request.setEmail("user@example.com");
        request.setPassword("pass123");
        request.setRoles(roles);
        
        assertEquals("user123", request.getUsername());
        assertEquals("user@example.com", request.getEmail());
        assertEquals("pass123", request.getPassword());
        assertEquals(roles, request.getRoles());
    }

    @Test
    void testEqualsAndHashCode() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        
        SignupRequest request1 = new SignupRequest("user", "user@example.com", "pass123", roles);
        SignupRequest request2 = new SignupRequest("user", "user@example.com", "pass123", roles);
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testNotEquals() {
        SignupRequest request1 = new SignupRequest("user1", "user1@example.com", "pass1", null);
        SignupRequest request2 = new SignupRequest("user2", "user2@example.com", "pass2", null);
        
        assertNotEquals(request1, request2);
    }

    @Test
    void testToString() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        
        SignupRequest request = new SignupRequest("testuser", "test@example.com", "testpass", roles);
        
        String toString = request.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("SignupRequest"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("password=testpass"));
        assertTrue(toString.contains("roles="));
    }

    @Test
    void testAllFieldsInvalid() {
        SignupRequest request = new SignupRequest();
        // Leave all fields null
        
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        assertEquals(3, violations.size()); // username, email, password all required
        
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password is required")));
    }

    @Test
    void testWhitespaceValidation() {
        SignupRequest request = new SignupRequest();
        request.setUsername("   ");
        request.setEmail("   ");
        request.setPassword("   ");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
        // Username and password have additional size constraints
        assertEquals(5, violations.size());
        
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password must be between 6 and 40 characters")));
    }
}
