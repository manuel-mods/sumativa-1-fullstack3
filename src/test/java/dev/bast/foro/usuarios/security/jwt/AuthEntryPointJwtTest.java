package dev.bast.foro.usuarios.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @Mock
    private ServletOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        when(response.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    void testCommence() throws IOException {
        String errorMessage = "Authentication failed";
        String servletPath = "/api/protected";
        
        when(authException.getMessage()).thenReturn(errorMessage);
        when(request.getServletPath()).thenReturn(servletPath);
        
        authEntryPointJwt.commence(request, response, authException);
        
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        ArgumentCaptor<Map<String, Object>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        
    }

    @Test
    void testCommenceWithNullMessage() throws IOException {
        String servletPath = "/api/protected";
        
        when(authException.getMessage()).thenReturn(null);
        when(request.getServletPath()).thenReturn(servletPath);
        
        authEntryPointJwt.commence(request, response, authException);
        
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
    }

    @Test
    void testCommenceWithEmptyPath() throws IOException {
        String errorMessage = "Authentication failed";
        
        when(authException.getMessage()).thenReturn(errorMessage);
        when(request.getServletPath()).thenReturn("");
        
        authEntryPointJwt.commence(request, response, authException);
        
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    
    }


    

    @Test
    void testResponseBody() throws IOException {
        String errorMessage = "Invalid token";
        String servletPath = "/api/users";
        
        when(authException.getMessage()).thenReturn(errorMessage);
        when(request.getServletPath()).thenReturn(servletPath);
        
        // Create a custom output stream to capture the response
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ServletOutputStream customOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                baos.write(b);
            }
            
            @Override
            public boolean isReady() {
                return true;
            }
            
            @Override
            public void setWriteListener(jakarta.servlet.WriteListener listener) {
                // Not implemented for testing
            }
        };
        
        when(response.getOutputStream()).thenReturn(customOutputStream);
        
        authEntryPointJwt.commence(request, response, authException);
        
        // Parse the written JSON
        String jsonResponse = baos.toString();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseBody = mapper.readValue(jsonResponse, Map.class);
        
        assertEquals(401, responseBody.get("status"));
        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
        assertEquals(servletPath, responseBody.get("path"));
    }

    @Test
    void testMultipleCommenceCalls() throws IOException {
        String errorMessage1 = "First error";
        String errorMessage2 = "Second error";
        String servletPath = "/api/test";
        
        when(authException.getMessage()).thenReturn(errorMessage1, errorMessage2);
        when(request.getServletPath()).thenReturn(servletPath);
        
        authEntryPointJwt.commence(request, response, authException);
        authEntryPointJwt.commence(request, response, authException);
        
        verify(response, times(2)).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response, times(2)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
    }

    @Test
    void testCommenceWithSpecialCharactersInMessage() throws IOException {
        String errorMessage = "Authentication failed: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        String servletPath = "/api/special";
        
        when(authException.getMessage()).thenReturn(errorMessage);
        when(request.getServletPath()).thenReturn(servletPath);
        
        authEntryPointJwt.commence(request, response, authException);
        
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
    }

    @Test
    void testCommenceWithLongPath() throws IOException {
        String errorMessage = "Authentication failed";
        String longPath = "/api/v1/users/profile/settings/security/authentication/tokens/refresh";
        
        when(authException.getMessage()).thenReturn(errorMessage);
        when(request.getServletPath()).thenReturn(longPath);
        
        authEntryPointJwt.commence(request, response, authException);
        
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    
    }
}
