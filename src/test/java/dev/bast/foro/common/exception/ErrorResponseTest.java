package dev.bast.foro.common.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testNoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        assertNotNull(errorResponse);
        assertEquals(0, errorResponse.getStatus());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getPath());
        assertNull(errorResponse.getTimestamp());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getMessage());
        assertEquals("/api/users/123", errorResponse.getPath());
        assertEquals(now, errorResponse.getTimestamp());
    }

    @Test
    void testSettersAndGetters() {
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();
        
        errorResponse.setStatus(500);
        errorResponse.setMessage("Internal Server Error");
        errorResponse.setPath("/api/error");
        errorResponse.setTimestamp(now);
        
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getMessage());
        assertEquals("/api/error", errorResponse.getPath());
        assertEquals(now, errorResponse.getTimestamp());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse error1 = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        ErrorResponse error2 = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        
        assertEquals(error1, error2);
        assertEquals(error1.hashCode(), error2.hashCode());
    }

    @Test
    void testNotEquals() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse error1 = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        ErrorResponse error2 = new ErrorResponse(500, "Server Error", "/api/error", now);
        
        assertNotEquals(error1, error2);
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        
        String toString = errorResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ErrorResponse"));
        assertTrue(toString.contains("status=404"));
        assertTrue(toString.contains("message=Not Found"));
        assertTrue(toString.contains("path=/api/users/123"));
        assertTrue(toString.contains("timestamp="));
    }

    @Test
    void testDifferentTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusMinutes(1);
        
        ErrorResponse error1 = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        ErrorResponse error2 = new ErrorResponse(404, "Not Found", "/api/users/123", later);
        
        assertNotEquals(error1, error2);
    }

    @Test
    void testDifferentStatuses() {
        LocalDateTime now = LocalDateTime.now();
        
        ErrorResponse error1 = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        ErrorResponse error2 = new ErrorResponse(403, "Not Found", "/api/users/123", now);
        
        assertNotEquals(error1, error2);
    }

    @Test
    void testDifferentMessages() {
        LocalDateTime now = LocalDateTime.now();
        
        ErrorResponse error1 = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        ErrorResponse error2 = new ErrorResponse(404, "User Not Found", "/api/users/123", now);
        
        assertNotEquals(error1, error2);
    }

    @Test
    void testDifferentPaths() {
        LocalDateTime now = LocalDateTime.now();
        
        ErrorResponse error1 = new ErrorResponse(404, "Not Found", "/api/users/123", now);
        ErrorResponse error2 = new ErrorResponse(404, "Not Found", "/api/users/456", now);
        
        assertNotEquals(error1, error2);
    }

    @Test
    void testNullValues() {
        ErrorResponse errorResponse = new ErrorResponse(200, null, null, null);
        
        assertEquals(200, errorResponse.getStatus());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getPath());
        assertNull(errorResponse.getTimestamp());
    }
}
