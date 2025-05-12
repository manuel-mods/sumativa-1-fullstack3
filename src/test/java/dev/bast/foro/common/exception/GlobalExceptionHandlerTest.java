package dev.bast.foro.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolationException constraintViolationException;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceNotFoundException(exception, webRequest);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals("uri=/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

  
    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(exception, webRequest);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Access denied. You don't have permission to access this resource.", response.getBody().getMessage());
        assertEquals("uri=/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    
    @Test
    void testHandleGlobalException() {
        Exception exception = new Exception("General error");
        
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(exception, webRequest);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        assertEquals("uri=/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }



}