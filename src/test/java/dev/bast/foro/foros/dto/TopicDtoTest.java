package dev.bast.foro.foros.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TopicDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTopicDto() {
        LocalDateTime now = LocalDateTime.now();
        TopicDto dto = new TopicDto();
        dto.setId(1);
        dto.setTitle("Valid Title");
        dto.setContent("This is a valid content for the topic");
        dto.setUserId(1L);
        dto.setUsername("testuser");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setActive(true);

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        TopicDto dto = new TopicDto();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getContent());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        TopicDto dto = new TopicDto(1, "Title", "Content text here", 1L, "user", now, now, true);
        
        assertEquals(1, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Content text here", dto.getContent());
        assertEquals(1L, dto.getUserId());
        assertEquals("user", dto.getUsername());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
        assertTrue(dto.isActive());
    }

    @Test
    void testBlankTitleValidation() {
        TopicDto dto = new TopicDto();
        dto.setTitle("");
        dto.setContent("Valid content here");

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
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
        TopicDto dto = new TopicDto();
        dto.setTitle(null);
        dto.setContent("Valid content here");

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Title is required", violations.iterator().next().getMessage());
    }

    @Test
    void testTitleTooShortValidation() {
        TopicDto dto = new TopicDto();
        dto.setTitle("ab");
        dto.setContent("Valid content here");

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Title must be between 3 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testTitleTooLongValidation() {
        TopicDto dto = new TopicDto();
        String longTitle = "a".repeat(101);
        dto.setTitle(longTitle);
        dto.setContent("Valid content here");

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Title must be between 3 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankContentValidation() {
        TopicDto dto = new TopicDto();
        dto.setTitle("Valid Title");
        dto.setContent("");

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
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
        TopicDto dto = new TopicDto();
        dto.setTitle("Valid Title");
        dto.setContent(null);

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Content is required", violations.iterator().next().getMessage());
    }

    @Test
    void testContentTooShortValidation() {
        TopicDto dto = new TopicDto();
        dto.setTitle("Valid Title");
        dto.setContent("Too short");

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Content must be between 10 and 5000 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testContentTooLongValidation() {
        TopicDto dto = new TopicDto();
        dto.setTitle("Valid Title");
        String longContent = "a".repeat(5001);
        dto.setContent(longContent);

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Content must be between 10 and 5000 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testContentExactlyMinLength() {
        TopicDto dto = new TopicDto();
        dto.setTitle("Valid Title");
        dto.setContent("a".repeat(10));

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testContentExactlyMaxLength() {
        TopicDto dto = new TopicDto();
        dto.setTitle("Valid Title");
        dto.setContent("a".repeat(5000));

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testTitleExactlyMinLength() {
        TopicDto dto = new TopicDto();
        dto.setTitle("abc");
        dto.setContent("Valid content here");

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testTitleExactlyMaxLength() {
        TopicDto dto = new TopicDto();
        dto.setTitle("a".repeat(100));
        dto.setContent("Valid content here");

        Set<ConstraintViolation<TopicDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        TopicDto dto = new TopicDto();
        
        dto.setId(1);
        dto.setTitle("Test Title");
        dto.setContent("Test content here");
        dto.setUserId(1L);
        dto.setUsername("testuser");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setActive(true);
        
        assertEquals(1, dto.getId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test content here", dto.getContent());
        assertEquals(1L, dto.getUserId());
        assertEquals("testuser", dto.getUsername());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
        assertTrue(dto.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        TopicDto dto1 = new TopicDto();
        dto1.setId(1);
        dto1.setTitle("Title");
        dto1.setContent("Content here");
        
        TopicDto dto2 = new TopicDto();
        dto2.setId(1);
        dto2.setTitle("Title");
        dto2.setContent("Content here");
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        TopicDto dto = new TopicDto();
        dto.setId(1);
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("TopicDto"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("title=Test Title"));
        assertTrue(toString.contains("content=Test Content"));
    }
}
