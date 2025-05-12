package dev.bast.foro.usuarios.security.jwt;

import dev.bast.foro.usuarios.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;
    private String jwtSecret;
    private int jwtExpirationMs;

    @BeforeEach
    void setUp() {
        // Setup user details
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER"));
        userDetails = new UserDetailsImpl(
                1L, "testuser", "test@example.com", "password", authorities);

        // Setup JWT properties
        jwtSecret = "ZGVmYXVsdFNlY3JldEtleVdoaWNoU2hvdWxkQmVDaGFuZ2VkSW5Qcm9kdWN0aW9u"; // Base64 encoded defaultSecretKeyWhichShouldBeChangedInProduction
        jwtExpirationMs = 86400000; // 1 day in milliseconds

        // Set JwtUtils properties via reflection
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    void generateJwtToken_ShouldReturnValidToken() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUserNameFromJwtToken_ShouldReturnCorrectUsername() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void validateJwtToken_WithValidToken_ShouldReturnTrue() {
        // Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_WithInvalidSignature_ShouldReturnFalse() {
        // Given
        String invalidToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("ZGlmZmVyZW50U2VjcmV0S2V5ZGlmZmVyZW50U2VjcmV0S2V5")))
                .compact();

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithExpiredToken_ShouldReturnFalse() {
        // Given
        String expiredToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date(System.currentTimeMillis() - 2 * jwtExpirationMs))
                .expiration(new Date(System.currentTimeMillis() - jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .compact();

        // When
        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithEmptyToken_ShouldReturnFalse() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtUtils.validateJwtToken(emptyToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithMalformedToken_ShouldReturnFalse() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // Then
        assertFalse(isValid);
    }
}