package dev.bast.foro.usuarios.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        
        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testBlankUsernameValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testNullUsernameValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername(null);
        request.setPassword("password123");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankPasswordValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    void testNullPasswordValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword(null);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    void testBothFieldsBlank() {
        LoginRequest request = new LoginRequest();
        request.setUsername("");
        request.setPassword("");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
        
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password is required")));
    }

    @Test
    void testBothFieldsNull() {
        LoginRequest request = new LoginRequest();
        // Both fields are null by default

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
        
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password is required")));
    }

    @Test
    void testSettersAndGetters() {
        LoginRequest request = new LoginRequest();
        
        request.setUsername("user123");
        request.setPassword("pass456");
        
        assertEquals("user123", request.getUsername());
        assertEquals("pass456", request.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        LoginRequest request1 = new LoginRequest("user", "pass");
        LoginRequest request2 = new LoginRequest("user", "pass");
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testNotEquals() {
        LoginRequest request1 = new LoginRequest("user1", "pass1");
        LoginRequest request2 = new LoginRequest("user2", "pass2");
        
        assertNotEquals(request1, request2);
    }

    @Test
    void testNotEqualsWithDifferentUsername() {
        LoginRequest request1 = new LoginRequest("user1", "pass");
        LoginRequest request2 = new LoginRequest("user2", "pass");
        
        assertNotEquals(request1, request2);
    }

    @Test
    void testNotEqualsWithDifferentPassword() {
        LoginRequest request1 = new LoginRequest("user", "pass1");
        LoginRequest request2 = new LoginRequest("user", "pass2");
        
        assertNotEquals(request1, request2);
    }

    @Test
    void testToString() {
        LoginRequest request = new LoginRequest("testuser", "testpass");
        
        String toString = request.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("LoginRequest"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("password=testpass"));
    }

    @Test
    void testWhitespaceOnlyValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("   ");
        request.setPassword("   ");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size());
        
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password is required")));
    }

    @Test
    void testValidWithWhitespace() {
        LoginRequest request = new LoginRequest();
        request.setUsername("  user  ");
        request.setPassword("  pass  ");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        
        // Verify the values include whitespace
        assertEquals("  user  ", request.getUsername());
        assertEquals("  pass  ", request.getPassword());
    }

    @Test
    void testLongValues() {
        LoginRequest request = new LoginRequest();
        String longUsername = "a".repeat(1000);
        String longPassword = "p".repeat(1000);
        
        request.setUsername(longUsername);
        request.setPassword(longPassword);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        
        assertEquals(longUsername, request.getUsername());
        assertEquals(longPassword, request.getPassword());
    }
}
