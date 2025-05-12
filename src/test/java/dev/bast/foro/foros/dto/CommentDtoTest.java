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

class CommentDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCommentDto() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto dto = new CommentDto();
        dto.setId(1);
        dto.setContent("This is a valid comment content");
        dto.setTopicId(100);
        dto.setUserId(1L);
        dto.setUsername("testuser");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setActive(true);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        CommentDto dto = new CommentDto();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getContent());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto dto = new CommentDto(1, "Content", 100, 1L, "user", now, now, true);
        
        assertEquals(1, dto.getId());
        assertEquals("Content", dto.getContent());
        assertEquals(100, dto.getTopicId());
        assertEquals(1L, dto.getUserId());
        assertEquals("user", dto.getUsername());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
        assertTrue(dto.isActive());
    }

    @Test
    void testBlankContentValidation() {
        CommentDto dto = new CommentDto();
        dto.setContent("");

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);
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
        CommentDto dto = new CommentDto();
        dto.setContent(null);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Content is required", violations.iterator().next().getMessage());
    }

    @Test
    void testContentTooLongValidation() {
        CommentDto dto = new CommentDto();
        String longContent = "a".repeat(2001);
        dto.setContent(longContent);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Content must be between 1 and 2000 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testContentExactlyMaxLength() {
        CommentDto dto = new CommentDto();
        String maxContent = "a".repeat(2000);
        dto.setContent(maxContent);

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testContentExactlyMinLength() {
        CommentDto dto = new CommentDto();
        dto.setContent("a");

        Set<ConstraintViolation<CommentDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testGetAsLong() {
        CommentDto dto = new CommentDto();
        dto.setTopicId(100);
        
        assertEquals(100L, dto.getAsLong());
    }

    @Test
    void testGetAsLongWithNull() {
        CommentDto dto = new CommentDto();
        dto.setTopicId(null);
        
        assertThrows(NullPointerException.class, () -> dto.getAsLong());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto dto = new CommentDto();
        
        dto.setId(1);
        dto.setContent("Test content");
        dto.setTopicId(100);
        dto.setUserId(1L);
        dto.setUsername("testuser");
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        dto.setActive(true);
        
        assertEquals(1, dto.getId());
        assertEquals("Test content", dto.getContent());
        assertEquals(100, dto.getTopicId());
        assertEquals(1L, dto.getUserId());
        assertEquals("testuser", dto.getUsername());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
        assertTrue(dto.isActive());
    }

    @Test
    void testEqualsAndHashCode() {
        CommentDto dto1 = new CommentDto();
        dto1.setId(1);
        dto1.setContent("Content");
        
        CommentDto dto2 = new CommentDto();
        dto2.setId(1);
        dto2.setContent("Content");
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        CommentDto dto = new CommentDto();
        dto.setId(1);
        dto.setContent("Test");
        
        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CommentDto"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("content=Test"));
    }
}
