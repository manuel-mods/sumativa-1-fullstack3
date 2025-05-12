package dev.bast.foro.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testResponseStatusAnnotation() {
        Class<ResourceNotFoundException> clazz = ResourceNotFoundException.class;
        
        assertTrue(clazz.isAnnotationPresent(ResponseStatus.class));
        
        ResponseStatus annotation = clazz.getAnnotation(ResponseStatus.class);
        assertEquals(HttpStatus.NOT_FOUND, annotation.value());
    }

    @Test
    void testThrowException() {
        String message = "User not found with id: 123";
        
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException(message);
        });
    }

    @Test
    void testCatchException() {
        String message = "Topic not found";
        
        try {
            throw new ResourceNotFoundException(message);
        } catch (ResourceNotFoundException e) {
            assertEquals(message, e.getMessage());
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    void testStackTrace() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test exception");
        
        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    void testNullMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException(null);
        
        assertNull(exception.getMessage());
    }

    @Test
    void testEmptyMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("");
        
        assertEquals("", exception.getMessage());
    }

    @Test
    void testLongMessage() {
        String longMessage = "a".repeat(1000);
        ResourceNotFoundException exception = new ResourceNotFoundException(longMessage);
        
        assertEquals(longMessage, exception.getMessage());
        assertEquals(1000, exception.getMessage().length());
    }

    @Test
    void testExceptionChaining() {
        String message = "Original exception";
        ResourceNotFoundException originalException = new ResourceNotFoundException(message);
        
        RuntimeException wrappedException = new RuntimeException("Wrapped exception", originalException);
        
        assertEquals(originalException, wrappedException.getCause());
        assertEquals(message, wrappedException.getCause().getMessage());
    }

    @Test
    void testExceptionInheritance() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testMultipleExceptions() {
        ResourceNotFoundException exception1 = new ResourceNotFoundException("Exception 1");
        ResourceNotFoundException exception2 = new ResourceNotFoundException("Exception 2");
        
        assertNotEquals(exception1, exception2);
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void testExceptionWithSpecialCharacters() {
        String specialMessage = "Resource not found: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        ResourceNotFoundException exception = new ResourceNotFoundException(specialMessage);
        
        assertEquals(specialMessage, exception.getMessage());
    }

    @Test
    void testExceptionWithUnicode() {
        String unicodeMessage = "Resource not found: 你好世界 🌍";
        ResourceNotFoundException exception = new ResourceNotFoundException(unicodeMessage);
        
        assertEquals(unicodeMessage, exception.getMessage());
    }

    @Test
    void testToString() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test message");
        
        String toString = exception.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ResourceNotFoundException"));
        assertTrue(toString.contains("Test message"));
    }
}
