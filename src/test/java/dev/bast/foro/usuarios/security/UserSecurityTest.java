package dev.bast.foro.usuarios.security;

import dev.bast.foro.usuarios.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSecurityTest {

    private UserSecurity userSecurity;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userSecurity = new UserSecurity();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testIsCurrentUserWhenAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);

        assertTrue(userSecurity.isCurrentUser(1L));
        assertFalse(userSecurity.isCurrentUser(2L));
    }

    @Test
    void testIsCurrentUserWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        assertFalse(userSecurity.isCurrentUser(1L));
    }

    @Test
    void testIsCurrentUserWhenAuthenticationNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        
        assertFalse(userSecurity.isCurrentUser(1L));
    }

    @Test
    void testIsCurrentUserWhenPrincipalNotUserDetailsImpl() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        assertFalse(userSecurity.isCurrentUser(1L));
    }

    @Test
    void testIsCommentOwnerOrAdminWhenOwner() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(userDetails.getAuthorities()).thenReturn(authorities);

        assertTrue(userSecurity.isCommentOwnerOrAdmin(1L, 100L));
        assertFalse(userSecurity.isCommentOwnerOrAdmin(2L, 100L));
    }

    @Test
    void testIsCommentOwnerOrAdminWhenAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(userDetails.getAuthorities()).thenReturn(authorities);

        assertTrue(userSecurity.isCommentOwnerOrAdmin(2L, 100L)); // Not owner but admin
    }

    @Test
    void testIsCurrentUserOrAdminWhenCurrentUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(userDetails.getAuthorities()).thenReturn(authorities);

        assertTrue(userSecurity.isCurrentUserOrAdmin(1L));
        assertFalse(userSecurity.isCurrentUserOrAdmin(2L));
    }

    @Test
    void testIsCurrentUserOrAdminWhenAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(userDetails.getAuthorities()).thenReturn(authorities);

        assertTrue(userSecurity.isCurrentUserOrAdmin(2L)); // Not current user but admin
    }

    @Test
    void testIsCurrentUserOrAdminWithoutParameter() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(userDetails.getAuthorities()).thenReturn(authorities);

        assertTrue(userSecurity.isCurrentUserOrAdmin());
    }

    @Test
    void testIsCurrentUserOrAdminWithoutParameterNotAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(userDetails.getAuthorities()).thenReturn(authorities);

        assertFalse(userSecurity.isCurrentUserOrAdmin());
    }

    @Test
    void testGetCurrentUserId() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(123L);

        assertEquals(123L, userSecurity.getCurrentUserId());
    }

    @Test
    void testGetCurrentUserIdWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        assertNull(userSecurity.getCurrentUserId());
    }

    @Test
    void testGetCurrentUsername() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        assertEquals("testuser", userSecurity.getCurrentUsername());
    }

    @Test
    void testGetCurrentUsernameWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        assertNull(userSecurity.getCurrentUsername());
    }

    @Test
    void testGetCurrentEmail() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getEmail()).thenReturn("test@example.com");

        assertEquals("test@example.com", userSecurity.getCurrentEmail());
    }

    @Test
    void testGetCurrentEmailWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        assertNull(userSecurity.getCurrentEmail());
    }

    @Test
    void testIsAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        assertTrue(userSecurity.isAuthenticated());
    }

    @Test
    void testIsAuthenticatedWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        assertFalse(userSecurity.isAuthenticated());
    }

    @Test
    void testHasRole() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        Collection authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        when(authentication.getAuthorities()).thenReturn(authorities);
        
        assertTrue(userSecurity.hasRole("ROLE_USER"));
        assertTrue(userSecurity.hasRole("ROLE_ADMIN"));
        assertFalse(userSecurity.hasRole("ROLE_MODERATOR"));
    }

    @Test
    void testHasRoleWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        assertFalse(userSecurity.hasRole("ROLE_USER"));
    }

    @Test
    void testHasAnyRole() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        Collection  authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        when(authentication.getAuthorities()).thenReturn(authorities);
        
        assertTrue(userSecurity.hasAnyRole("ROLE_USER", "ROLE_ADMIN"));
        assertTrue(userSecurity.hasAnyRole("ROLE_MODERATOR", "ROLE_USER"));
        assertFalse(userSecurity.hasAnyRole("ROLE_ADMIN", "ROLE_MODERATOR"));
    }

    @Test
    void testHasAnyRoleWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        assertFalse(userSecurity.hasAnyRole("ROLE_USER", "ROLE_ADMIN"));
    }

    @Test
    void testHasAllRoles() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        Collection  authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        when(authentication.getAuthorities()).thenReturn(authorities);
        
        assertTrue(userSecurity.hasAllRoles("ROLE_USER", "ROLE_ADMIN"));
        assertFalse(userSecurity.hasAllRoles("ROLE_USER", "ROLE_MODERATOR"));
    }

    @Test
    void testHasAllRolesWhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        
        assertFalse(userSecurity.hasAllRoles("ROLE_USER"));
    }

    @Test
    void testIsUserInRole() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        Collection  authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        when(authentication.getAuthorities()).thenReturn(authorities);
        
        assertTrue(userSecurity.isUserInRole("ROLE_USER"));
        assertFalse(userSecurity.isUserInRole("ROLE_ADMIN"));
    }

    @Test
    void testIsUserInAnyRole() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        Collection  authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        when(authentication.getAuthorities()).thenReturn(authorities);
        
        assertTrue(userSecurity.isUserInAnyRole("ROLE_USER", "ROLE_ADMIN"));
        assertFalse(userSecurity.isUserInAnyRole("ROLE_ADMIN", "ROLE_MODERATOR"));
    }

    @Test
    void testIsUserInAllRoles() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        Collection  authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        when(authentication.getAuthorities()).thenReturn(authorities);
        
        assertTrue(userSecurity.isUserInAllRoles("ROLE_USER", "ROLE_ADMIN"));
        assertFalse(userSecurity.isUserInAllRoles("ROLE_USER", "ROLE_MODERATOR"));
    }

    @Test
    void testIsUserInRoleWithUserId() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(userDetails.getAuthorities()).thenReturn(authorities);
        
        assertTrue(userSecurity.isUserInRole("ROLE_USER", 1L));
        assertFalse(userSecurity.isUserInRole("ROLE_USER", 2L)); // Different user ID
        assertFalse(userSecurity.isUserInRole("ROLE_ADMIN", 1L)); // Wrong role
    }

    @Test
    void testIsUserInAnyRoleWithUserId() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(userDetails.getAuthorities()).thenReturn(authorities);
        
        assertTrue(userSecurity.isUserInAnyRole(1L, "ROLE_USER", "ROLE_ADMIN"));
        assertFalse(userSecurity.isUserInAnyRole(2L, "ROLE_USER", "ROLE_ADMIN")); // Different user ID
        assertFalse(userSecurity.isUserInAnyRole(1L, "ROLE_ADMIN", "ROLE_MODERATOR")); // Wrong roles
    }

    @Test
    void testIsTopicOwnerOrAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(userDetails.getAuthorities()).thenReturn(authorities);

        assertTrue(userSecurity.isTopicOwnerOrAdmin(1L, 100L)); // Owner
        assertFalse(userSecurity.isTopicOwnerOrAdmin(2L, 100L)); // Not owner
    }

    @Test
    void testIsTopicOwnerOrAdminWhenAdmin() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        
        Collection authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(userDetails.getAuthorities()).thenReturn(authorities);

        assertTrue(userSecurity.isTopicOwnerOrAdmin(2L, 100L)); // Not owner but admin
    }

    @Test
    void testAllMethodsWhenPrincipalNotUserDetailsImpl() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        
        assertFalse(userSecurity.isCurrentUser(1L));
        assertFalse(userSecurity.isCommentOwnerOrAdmin(1L, 1L));
        assertFalse(userSecurity.isCurrentUserOrAdmin(1L));
        assertFalse(userSecurity.isCurrentUserOrAdmin());
        assertNull(userSecurity.getCurrentUserId());
        assertNull(userSecurity.getCurrentUsername());
        assertNull(userSecurity.getCurrentEmail());
        assertFalse(userSecurity.isTopicOwnerOrAdmin(1L, 1L));
        assertFalse(userSecurity.isUserInRole("ROLE_USER", 1L));
        assertFalse(userSecurity.isUserInAnyRole(1L, "ROLE_USER"));
    }
}
