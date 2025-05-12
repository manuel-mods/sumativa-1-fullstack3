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

class CommentTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setContent("This is a valid comment content");
        comment.setTopicId(100);
        comment.setUserId(1L);
        comment.setUsername("testuser");
        comment.setActive(true);

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        Comment comment = new Comment();
        assertNotNull(comment);
        assertNull(comment.getId());
        assertNull(comment.getContent());
        assertTrue(comment.isActive()); // default value
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment(1, "Content", 100, 1L, "user", now, now, true);
        
        assertEquals(1, comment.getId());
        assertEquals("Content", comment.getContent());
        assertEquals(100, comment.getTopicId());
        assertEquals(1L, comment.getUserId());
        assertEquals("user", comment.getUsername());
        assertEquals(now, comment.getCreatedAt());
        assertEquals(now, comment.getUpdatedAt());
        assertTrue(comment.isActive());
    }

    @Test
    void testBlankContentValidation() {
        Comment comment = new Comment();
        comment.setContent("");

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
        assertEquals(2, violations.size());
        
        boolean hasNotBlankViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Content is required"));
        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Content must be between 1 and 2000 characters"));
        
        assertTrue(hasNotBlankViolation);
        assertTrue(hasSizeViolation);
    }

    @Test
    void testNullContentValidation() {
        Comment comment = new Comment();
        comment.setContent(null);

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
        assertEquals(1, violations.size());
        assertEquals("Content is required", violations.iterator().next().getMessage());
    }

    @Test
    void testContentTooLongValidation() {
        Comment comment = new Comment();
        String longContent = "a".repeat(2001);
        comment.setContent(longContent);

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
        assertEquals(1, violations.size());
        assertEquals("Content must be between 1 and 2000 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testContentExactlyMaxLength() {
        Comment comment = new Comment();
        String maxContent = "a".repeat(2000);
        comment.setContent(maxContent);

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testContentExactlyMinLength() {
        Comment comment = new Comment();
        comment.setContent("a");

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testOnCreateSetsTimestamps() {
        Comment comment = new Comment();
        comment.setContent("Test content");
        
        assertNull(comment.getCreatedAt());
        assertNull(comment.getUpdatedAt());
        
        comment.onCreate();
        
        assertNotNull(comment.getCreatedAt());
        assertNotNull(comment.getUpdatedAt());
        // assertEquals(comment.getCreatedAt(), comment.getUpdatedAt());
    }

    @Test
    void testOnUpdateModifiesUpdatedAt() {
        Comment comment = new Comment();
        comment.setContent("Test content");
        
        LocalDateTime initialTime = LocalDateTime.now().minusHours(1);
        comment.setCreatedAt(initialTime);
        comment.setUpdatedAt(initialTime);
        
        comment.onUpdate();
        
        assertEquals(initialTime, comment.getCreatedAt());
        assertNotEquals(initialTime, comment.getUpdatedAt());
        assertTrue(comment.getUpdatedAt().isAfter(initialTime));
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment();
        
        comment.setId(1);
        comment.setContent("Test content");
        comment.setTopicId(100);
        comment.setUserId(1L);
        comment.setUsername("testuser");
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        comment.setActive(false);
        
        assertEquals(1, comment.getId());
        assertEquals("Test content", comment.getContent());
        assertEquals(100, comment.getTopicId());
        assertEquals(1L, comment.getUserId());
        assertEquals("testuser", comment.getUsername());
        assertEquals(now, comment.getCreatedAt());
        assertEquals(now, comment.getUpdatedAt());
        assertFalse(comment.isActive());
    }

    @Test
    void testDefaultActiveValue() {
        Comment comment = new Comment();
        assertTrue(comment.isActive());
        
        // Test with all args constructor
        LocalDateTime now = LocalDateTime.now();
        Comment comment2 = new Comment(1, "Content", 100, 1L, "user", now, now, false);
        assertFalse(comment2.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1);
        comment1.setContent("Content");
        
        Comment comment2 = new Comment();
        comment2.setId(1);
        comment2.setContent("Content");
        
        assertEquals(comment1, comment2);
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void testNotEquals() {
        Comment comment1 = new Comment();
        comment1.setId(1);
        
        Comment comment2 = new Comment();
        comment2.setId(2);
        
        assertNotEquals(comment1, comment2);
    }

    @Test
    void testToString() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setContent("Test");
        comment.setTopicId(100);
        
        String toString = comment.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Comment"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("content=Test"));
        assertTrue(toString.contains("topicId=100"));
    }

    @Test
    void testEntityAnnotations() {
        Class<Comment> clazz = Comment.class;
        
        assertTrue(clazz.isAnnotationPresent(jakarta.persistence.Entity.class));
        assertTrue(clazz.isAnnotationPresent(jakarta.persistence.Table.class));
        
        jakarta.persistence.Table tableAnnotation = clazz.getAnnotation(jakarta.persistence.Table.class);
        assertEquals("comments", tableAnnotation.name());
    }

    @Test
    void testFieldAnnotations() throws NoSuchFieldException {
        Class<Comment> clazz = Comment.class;
        
        assertTrue(clazz.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.Id.class));
        assertTrue(clazz.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.GeneratedValue.class));
        
        jakarta.persistence.GeneratedValue genValue = clazz.getDeclaredField("id")
                .getAnnotation(jakarta.persistence.GeneratedValue.class);
        assertEquals(jakarta.persistence.GenerationType.IDENTITY, genValue.strategy());
        
        jakarta.persistence.Column contentColumn = clazz.getDeclaredField("content")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("TEXT", contentColumn.columnDefinition());
        
        jakarta.persistence.Column topicColumn = clazz.getDeclaredField("topicId")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("topic_id", topicColumn.name());
        
        jakarta.persistence.Column userColumn = clazz.getDeclaredField("userId")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("user_id", userColumn.name());
        
        jakarta.persistence.Column createdColumn = clazz.getDeclaredField("createdAt")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("created_at", createdColumn.name());
        
        jakarta.persistence.Column updatedColumn = clazz.getDeclaredField("updatedAt")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals("updated_at", updatedColumn.name());
    }

    @Test
    void testLifecycleCallbackAnnotations() throws NoSuchMethodException {
        Class<Comment> clazz = Comment.class;
        
        assertTrue(clazz.getDeclaredMethod("onCreate").isAnnotationPresent(jakarta.persistence.PrePersist.class));
        assertTrue(clazz.getDeclaredMethod("onUpdate").isAnnotationPresent(jakarta.persistence.PreUpdate.class));
    }
}
