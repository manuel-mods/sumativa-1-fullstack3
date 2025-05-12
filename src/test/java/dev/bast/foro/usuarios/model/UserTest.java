package dev.bast.foro.usuarios.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setActive(true);
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertTrue(user.isActive()); // default value
        assertNotNull(user.getRoles()); // initialized as empty HashSet
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName(Role.ERole.ROLE_USER);
        roles.add(role);
        
        User user = new User(1L, "testuser", "test@example.com", "password", true, now, now, roles);
        
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.isActive());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void testBlankUsernameValidation() {
        User user = new User();
        user.setUsername("");
        user.setEmail("test@example.com");
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
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
        User user = new User();
        user.setUsername(null);
        user.setEmail("test@example.com");
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooShortValidation() {
        User user = new User();
        user.setUsername("ab");
        user.setEmail("test@example.com");
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Username must be between 3 and 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooLongValidation() {
        User user = new User();
        String longUsername = "a".repeat(51);
        user.setUsername(longUsername);
        user.setEmail("test@example.com");
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Username must be between 3 and 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankEmailValidation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("");
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        
        boolean hasNotBlankViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email is required"));
        
        assertTrue(hasNotBlankViolation);
    }

    @Test
    void testNullEmailValidation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail(null);
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email is required", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmailFormatValidation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("invalid-email");
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankPasswordValidation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    void testNullPasswordValidation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    void testOnCreateSetsTimestamps() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
        
        user.onCreate();
        
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        // assertEquals(user.getCreatedAt().format(null), user.getUpdatedAt());
    }

    @Test
    void testOnUpdateModifiesUpdatedAt() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        
        LocalDateTime initialTime = LocalDateTime.now().minusHours(1);
        user.setCreatedAt(initialTime);
        user.setUpdatedAt(initialTime);
        
        user.onUpdate();
        
        assertEquals(initialTime, user.getCreatedAt());
        assertNotEquals(initialTime, user.getUpdatedAt());
        assertTrue(user.getUpdatedAt().isAfter(initialTime));
    }

    @Test
    void testDefaultActiveValue() {
        User user = new User();
        assertTrue(user.isActive());
    }

    @Test
    void testRolesManipulation() {
        User user = new User();
        Set<Role> roles = new HashSet<>();
        
        assertTrue(user.getRoles().isEmpty());
        
        Role userRole = new Role();
        userRole.setName(Role.ERole.ROLE_USER);
        roles.add(userRole);
        
        user.setRoles(roles);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(userRole));
        
        Role adminRole = new Role();
        adminRole.setName(Role.ERole.ROLE_ADMIN);
        user.getRoles().add(adminRole);
        
        assertEquals(2, user.getRoles().size());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        Set<Role> roles = new HashSet<>();
        
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setActive(false);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setRoles(roles);
        
        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertFalse(user.isActive());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertEquals(roles, user.getRoles());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser");
        user1.setEmail("test@example.com");
        
        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("testuser");
        user2.setEmail("test@example.com");
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testNotEquals() {
        User user1 = new User();
        user1.setId(1L);
        
        User user2 = new User();
        user2.setId(2L);
        
        assertNotEquals(user1, user2);
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("User"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("email=test@example.com"));
    }

    @Test
    void testEntityAnnotations() {
        Class<User> clazz = User.class;
        
        assertTrue(clazz.isAnnotationPresent(jakarta.persistence.Entity.class));
        assertTrue(clazz.isAnnotationPresent(jakarta.persistence.Table.class));
        
        jakarta.persistence.Table tableAnnotation = clazz.getAnnotation(jakarta.persistence.Table.class);
        assertEquals("users", tableAnnotation.name());
    }

    @Test
    void testFieldAnnotations() throws NoSuchFieldException {
        Class<User> clazz = User.class;
        
        assertTrue(clazz.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.Id.class));
        assertTrue(clazz.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.GeneratedValue.class));
        
        jakarta.persistence.GeneratedValue genValue = clazz.getDeclaredField("id")
                .getAnnotation(jakarta.persistence.GeneratedValue.class);
        assertEquals(jakarta.persistence.GenerationType.IDENTITY, genValue.strategy());
        
        jakarta.persistence.Column usernameColumn = clazz.getDeclaredField("username")
                .getAnnotation(jakarta.persistence.Column.class);
        assertTrue(usernameColumn.unique());
        
        jakarta.persistence.Column emailColumn = clazz.getDeclaredField("email")
                .getAnnotation(jakarta.persistence.Column.class);
        assertTrue(emailColumn.unique());
        
        jakarta.persistence.Column createdColumn = clazz.getDeclaredField("createdAt")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("created_at", createdColumn.name());
        
        jakarta.persistence.Column updatedColumn = clazz.getDeclaredField("updatedAt")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("updated_at", updatedColumn.name());
    }

    @Test
    void testRolesRelationshipAnnotations() throws NoSuchFieldException {
        Class<User> clazz = User.class;
        
        assertTrue(clazz.getDeclaredField("roles").isAnnotationPresent(jakarta.persistence.ManyToMany.class));
        assertTrue(clazz.getDeclaredField("roles").isAnnotationPresent(jakarta.persistence.JoinTable.class));
        
        jakarta.persistence.ManyToMany manyToMany = clazz.getDeclaredField("roles")
                .getAnnotation(jakarta.persistence.ManyToMany.class);
        assertEquals(jakarta.persistence.FetchType.EAGER, manyToMany.fetch());
        
        jakarta.persistence.JoinTable joinTable = clazz.getDeclaredField("roles")
                .getAnnotation(jakarta.persistence.JoinTable.class);
        assertEquals("user_roles", joinTable.name());
        
        jakarta.persistence.JoinColumn[] joinColumns = joinTable.joinColumns();
        assertEquals(1, joinColumns.length);
        assertEquals("user_id", joinColumns[0].name());
        
        jakarta.persistence.JoinColumn[] inverseJoinColumns = joinTable.inverseJoinColumns();
        assertEquals(1, inverseJoinColumns.length);
        assertEquals("role_id", inverseJoinColumns[0].name());
    }

    @Test
    void testLifecycleCallbackAnnotations() throws NoSuchMethodException {
        Class<User> clazz = User.class;
        
        assertTrue(clazz.getDeclaredMethod("onCreate").isAnnotationPresent(jakarta.persistence.PrePersist.class));
        assertTrue(clazz.getDeclaredMethod("onUpdate").isAnnotationPresent(jakarta.persistence.PreUpdate.class));
    }

    @Test
    void testUsernameExactlyMinLength() {
        User user = new User();
        user.setUsername("abc");
        user.setEmail("test@example.com");
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testUsernameExactlyMaxLength() {
        User user = new User();
        user.setUsername("a".repeat(50));
        user.setEmail("test@example.com");
        user.setPassword("password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidEmailFormats() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        
        String[] validEmails = {
            "user@domain.com",
            "user.name@domain.com",
            "user+tag@domain.co.uk",
            "user123@sub.domain.com"
        };
        
        for (String email : validEmails) {
            user.setEmail(email);
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
        }
    }

    @Test
    void testActiveToggle() {
        User user = new User();
        
        // Test default
        assertTrue(user.isActive());
        
        // Test setting to false
        user.setActive(false);
        assertFalse(user.isActive());
        
        // Test setting back to true
        user.setActive(true);
        assertTrue(user.isActive());
    }

    @Test
    void testInitialRolesNotNull() {
        User user = new User();
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void testMultipleValidationErrors() {
        User user = new User();
        // Leave all required fields null/empty
        
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(3, violations.size()); // username, email, password all required
        
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password is required")));
    }
}
