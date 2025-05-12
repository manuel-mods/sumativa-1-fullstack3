package dev.bast.foro.foros.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTopic() {
        Topic topic = new Topic();
        topic.setId(1);
        topic.setTitle("Valid Title");
        topic.setContent("This is a valid content for the topic");
        topic.setUserId(1L);
        topic.setUsername("testuser");
        topic.setActive(true);

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        Topic topic = new Topic();
        assertNotNull(topic);
        assertNull(topic.getId());
        assertNull(topic.getTitle());
        assertNull(topic.getContent());
        assertTrue(topic.isActive()); // default value
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Topic topic = new Topic(1, "Title", "Content text here", 1L, "user", now, now, true);
        
        assertEquals(1, topic.getId());
        assertEquals("Title", topic.getTitle());
        assertEquals("Content text here", topic.getContent());
        assertEquals(1L, topic.getUserId());
        assertEquals("user", topic.getUsername());
        assertEquals(now, topic.getCreatedAt());
        assertEquals(now, topic.getUpdatedAt());
        assertTrue(topic.isActive());
    }

    @Test
    void testBlankTitleValidation() {
        Topic topic = new Topic();
        topic.setTitle("");
        topic.setContent("Valid content here");

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertEquals(2, violations.size());
        
        boolean hasNotBlankViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Title is required"));
        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Title must be between 3 and 100 characters"));
        
        assertTrue(hasNotBlankViolation);
        assertTrue(hasSizeViolation);
    }

    @Test
    void testNullTitleValidation() {
        Topic topic = new Topic();
        topic.setTitle(null);
        topic.setContent("Valid content here");

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertEquals(1, violations.size());
        assertEquals("Title is required", violations.iterator().next().getMessage());
    }

    @Test
    void testTitleTooShortValidation() {
        Topic topic = new Topic();
        topic.setTitle("ab");
        topic.setContent("Valid content here");

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertEquals(1, violations.size());
        assertEquals("Title must be between 3 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testTitleTooLongValidation() {
        Topic topic = new Topic();
        String longTitle = "a".repeat(101);
        topic.setTitle(longTitle);
        topic.setContent("Valid content here");

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertEquals(1, violations.size());
        assertEquals("Title must be between 3 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankContentValidation() {
        Topic topic = new Topic();
        topic.setTitle("Valid Title");
        topic.setContent("");

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertEquals(2, violations.size());
        
        boolean hasNotBlankViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Content is required"));
        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Content must be between 10 and 5000 characters"));
        
        assertTrue(hasNotBlankViolation);
        assertTrue(hasSizeViolation);
    }

    @Test
    void testNullContentValidation() {
        Topic topic = new Topic();
        topic.setTitle("Valid Title");
        topic.setContent(null);

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertEquals(1, violations.size());
        assertEquals("Content is required", violations.iterator().next().getMessage());
    }

    @Test
    void testContentTooShortValidation() {
        Topic topic = new Topic();
        topic.setTitle("Valid Title");
        topic.setContent("Too short");

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertEquals(1, violations.size());
        assertEquals("Content must be between 10 and 5000 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testContentTooLongValidation() {
        Topic topic = new Topic();
        topic.setTitle("Valid Title");
        String longContent = "a".repeat(5001);
        topic.setContent(longContent);

        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertEquals(1, violations.size());
        assertEquals("Content must be between 10 and 5000 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testOnCreateSetsTimestamps() {
        Topic topic = new Topic();
        topic.setTitle("Test Title");
        topic.setContent("Test content here");
        
        assertNull(topic.getCreatedAt());
        assertNull(topic.getUpdatedAt());
        
        topic.onCreate();
        
        assertNotNull(topic.getCreatedAt());
        assertNotNull(topic.getUpdatedAt());
        // assertEquals(topic.getCreatedAt(), topic.getUpdatedAt());
    }

    @Test
    void testOnUpdateModifiesUpdatedAt() {
        Topic topic = new Topic();
        topic.setTitle("Test Title");
        topic.setContent("Test content here");
        
        LocalDateTime initialTime = LocalDateTime.now().minusHours(1);
        topic.setCreatedAt(initialTime);
        topic.setUpdatedAt(initialTime);
        
        topic.onUpdate();
        
        assertEquals(initialTime, topic.getCreatedAt());
        assertNotEquals(initialTime, topic.getUpdatedAt());
        assertTrue(topic.getUpdatedAt().isAfter(initialTime));
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        Topic topic = new Topic();
        
        topic.setId(1);
        topic.setTitle("Test Title");
        topic.setContent("Test content here");
        topic.setUserId(1L);
        topic.setUsername("testuser");
        topic.setCreatedAt(now);
        topic.setUpdatedAt(now);
        topic.setActive(false);
        
        assertEquals(1, topic.getId());
        assertEquals("Test Title", topic.getTitle());
        assertEquals("Test content here", topic.getContent());
        assertEquals(1L, topic.getUserId());
        assertEquals("testuser", topic.getUsername());
        assertEquals(now, topic.getCreatedAt());
        assertEquals(now, topic.getUpdatedAt());
        assertFalse(topic.isActive());
    }

    @Test
    void testDefaultActiveValue() {
        Topic topic = new Topic();
        assertTrue(topic.isActive());
        
        // Test with all args constructor
        LocalDateTime now = LocalDateTime.now();
        Topic topic2 = new Topic(1, "Title", "Content", 1L, "user", now, now, false);
        assertFalse(topic2.isActive());
    }

    @Test
    void testBoundaryValues() {
        Topic topic = new Topic();
        
        // Test title at min length
        topic.setTitle("abc");
        topic.setContent("a".repeat(10));
        Set<ConstraintViolation<Topic>> violations = validator.validate(topic);
        assertTrue(violations.isEmpty());
        
        // Test title at max length
        topic.setTitle("a".repeat(100));
        topic.setContent("a".repeat(10));
        violations = validator.validate(topic);
        assertTrue(violations.isEmpty());
        
        // Test content at min length
        topic.setTitle("Valid Title");
        topic.setContent("a".repeat(10));
        violations = validator.validate(topic);
        assertTrue(violations.isEmpty());
        
        // Test content at max length
        topic.setTitle("Valid Title");
        topic.setContent("a".repeat(5000));
        violations = validator.validate(topic);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        Topic topic1 = new Topic();
        topic1.setId(1);
        topic1.setTitle("Title");
        topic1.setContent("Content here");
        
        Topic topic2 = new Topic();
        topic2.setId(1);
        topic2.setTitle("Title");
        topic2.setContent("Content here");
        
        assertEquals(topic1, topic2);
        assertEquals(topic1.hashCode(), topic2.hashCode());
    }

    @Test
    void testNotEquals() {
        Topic topic1 = new Topic();
        topic1.setId(1);
        
        Topic topic2 = new Topic();
        topic2.setId(2);
        
        assertNotEquals(topic1, topic2);
    }

    @Test
    void testToString() {
        Topic topic = new Topic();
        topic.setId(1);
        topic.setTitle("Test Title");
        topic.setContent("Test Content");
        
        String toString = topic.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Topic"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("title=Test Title"));
        assertTrue(toString.contains("content=Test Content"));
    }

    @Test
    void testEntityAnnotations() {
        Class<Topic> clazz = Topic.class;
        
        assertTrue(clazz.isAnnotationPresent(jakarta.persistence.Entity.class));
        assertTrue(clazz.isAnnotationPresent(jakarta.persistence.Table.class));
        
        jakarta.persistence.Table tableAnnotation = clazz.getAnnotation(jakarta.persistence.Table.class);
        assertEquals("topics", tableAnnotation.name());
    }

    @Test
    void testFieldAnnotations() throws NoSuchFieldException {
        Class<Topic> clazz = Topic.class;
        
        assertTrue(clazz.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.Id.class));
        assertTrue(clazz.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.GeneratedValue.class));
        
        jakarta.persistence.GeneratedValue genValue = clazz.getDeclaredField("id")
                .getAnnotation(jakarta.persistence.GeneratedValue.class);
        assertEquals(jakarta.persistence.GenerationType.IDENTITY, genValue.strategy());
        
        jakarta.persistence.Column contentColumn = clazz.getDeclaredField("content")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("TEXT", contentColumn.columnDefinition());
        
        jakarta.persistence.Column userColumn = clazz.getDeclaredField("userId")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("user_id", userColumn.name());
        
        jakarta.persistence.Column usernameColumn = clazz.getDeclaredField("username")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("username", usernameColumn.name());
        
        jakarta.persistence.Column createdColumn = clazz.getDeclaredField("createdAt")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("created_at", createdColumn.name());
        
        jakarta.persistence.Column updatedColumn = clazz.getDeclaredField("updatedAt")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("updated_at", updatedColumn.name());
    }

    @Test
    void testLifecycleCallbackAnnotations() throws NoSuchMethodException {
        Class<Topic> clazz = Topic.class;
        
        assertTrue(clazz.getDeclaredMethod("onCreate").isAnnotationPresent(jakarta.persistence.PrePersist.class));
        assertTrue(clazz.getDeclaredMethod("onUpdate").isAnnotationPresent(jakarta.persistence.PreUpdate.class));
    }
}
