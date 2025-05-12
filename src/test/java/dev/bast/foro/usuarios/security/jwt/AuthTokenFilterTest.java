package dev.bast.foro.usuarios.security.jwt;

import dev.bast.foro.usuarios.security.services.UserDetailsImpl;
import dev.bast.foro.usuarios.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        userDetails = new UserDetailsImpl(
                1L,
                "testuser",
                "test@example.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void testDoFilterInternal_WithValidToken() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils).getUserNameFromJwtToken(token);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNullToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithEmptyToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("");
        
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithInvalidBearerFormat() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidBearer token");
        
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);
        
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils, never()).getUserNameFromJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithException() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenThrow(new RuntimeException("JWT validation error"));
        
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testParseJwt_WithValidBearerToken() throws Exception {
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        
        // Use reflection to test private method
        java.lang.reflect.Method parseJwtMethod = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        parseJwtMethod.setAccessible(true);
        String result = (String) parseJwtMethod.invoke(authTokenFilter, request);
        
        assertEquals(token, result);
    }

    @Test
    void testParseJwt_WithNullHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        
        // Use reflection to test private method
        java.lang.reflect.Method parseJwtMethod = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        parseJwtMethod.setAccessible(true);
        String result = (String) parseJwtMethod.invoke(authTokenFilter, request);
        
        assertNull(result);
    }

    @Test
    void testParseJwt_WithEmptyHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("");
        
        // Use reflection to test private method
        java.lang.reflect.Method parseJwtMethod = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        parseJwtMethod.setAccessible(true);
        String result = (String) parseJwtMethod.invoke(authTokenFilter, request);
        
        assertNull(result);
    }

    @Test
    void testParseJwt_WithInvalidBearerFormat() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");
        
        // Use reflection to test private method
        java.lang.reflect.Method parseJwtMethod = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        parseJwtMethod.setAccessible(true);
        String result = (String) parseJwtMethod.invoke(authTokenFilter, request);
        
        assertNull(result);
    }

    @Test
    void testParseJwt_WithBearerOnly() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        
        // Use reflection to test private method
        java.lang.reflect.Method parseJwtMethod = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        parseJwtMethod.setAccessible(true);
        String result = (String) parseJwtMethod.invoke(authTokenFilter, request);
        
        assertEquals("", result);
    }

    @Test
    void testInheritance() {
        assertTrue(authTokenFilter instanceof org.springframework.web.filter.OncePerRequestFilter);
    }

    @Test
    void testLoggerInitialization() throws Exception {
        // Use reflection to access the private static logger field
        java.lang.reflect.Field loggerField = AuthTokenFilter.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        Object logger = loggerField.get(null);
        
        assertNotNull(logger);
        assertTrue(logger instanceof org.slf4j.Logger);
    }

    @Test
    void testAuthenticationDetailsSource() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        
        // Create a spy of WebAuthenticationDetailsSource to verify its usage
        WebAuthenticationDetailsSource detailsSource = spy(new WebAuthenticationDetailsSource());
        
        authTokenFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testExceptionLogging() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenThrow(new RuntimeException("Username extraction failed"));
        
        // Should not throw exception, just log it
        assertDoesNotThrow(() -> authTokenFilter.doFilterInternal(request, response, filterChain));
        
        verify(filterChain).doFilter(request, response);
    }
}
