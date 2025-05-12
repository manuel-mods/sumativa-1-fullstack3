package dev.bast.foro.usuarios.security.services;

import dev.bast.foro.usuarios.model.User;
import dev.bast.foro.usuarios.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void testConstructor() {
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", authorities);
        
        assertEquals(1L, userDetails.getId());
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
        assertEquals(authorities, userDetails.getAuthorities());
    }

    @Test
    void testBuildFromUser() {
        // Create user with roles
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        
        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName(Role.ERole.ROLE_USER);
        roles.add(userRole);
        
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName(Role.ERole.ROLE_ADMIN);
        roles.add(adminRole);
        
        user.setRoles(roles);
        
        // Build UserDetailsImpl
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        
        assertEquals(1L, userDetails.getId());
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
        
        // Check authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testBuildFromUserWithEmptyRoles() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void testGetters() {
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", authorities);
        
        assertEquals(1L, userDetails.getId());
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
        assertEquals(authorities, userDetails.getAuthorities());
    }

    @Test
    void testAccountStatus() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", List.of());
        
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testEquals() {
        UserDetailsImpl user1 = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", List.of());
        UserDetailsImpl user2 = new UserDetailsImpl(1L, "different", "different@example.com", "different", List.of());
        UserDetailsImpl user3 = new UserDetailsImpl(2L, "testuser", "test@example.com", "password", List.of());
        
        // Same ID means equal
        assertEquals(user1, user2);
        
        // Different ID means not equal
        assertNotEquals(user1, user3);
        
        // Self equals
        assertEquals(user1, user1);
        
        // Null not equal
        assertNotEquals(user1, null);
        
        // Different class not equal
        assertNotEquals(user1, "NotUserDetailsImpl");
    }

    @Test
    void testHashCode() {
        UserDetailsImpl user1 = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", List.of());
        UserDetailsImpl user2 = new UserDetailsImpl(1L, "different", "different@example.com", "different", List.of());
        UserDetailsImpl user3 = new UserDetailsImpl(2L, "testuser", "test@example.com", "password", List.of());
        
        // Same ID means same hash code
        assertEquals(user1.hashCode(), user2.hashCode());
        
        // Different ID likely means different hash code (not guaranteed, but very likely)
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testJsonIgnoreOnPassword() throws NoSuchFieldException {
        // Verify that password field has JsonIgnore annotation
        Class<UserDetailsImpl> clazz = UserDetailsImpl.class;
        assertTrue(clazz.getDeclaredField("password")
                .isAnnotationPresent(com.fasterxml.jackson.annotation.JsonIgnore.class));
    }

    @Test
    void testSerialVersionUID() throws NoSuchFieldException, IllegalAccessException {
        Class<UserDetailsImpl> clazz = UserDetailsImpl.class;
        java.lang.reflect.Field serialVersionUIDField = clazz.getDeclaredField("serialVersionUID");
        serialVersionUIDField.setAccessible(true);
        long serialVersionUID = (long) serialVersionUIDField.get(null);
        assertEquals(1L, serialVersionUID);
    }

    @Test
    void testImplementsUserDetails() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", List.of());
        assertTrue(userDetails instanceof org.springframework.security.core.userdetails.UserDetails);
    }

    @Test
    void testAuthoritiesOrder() {
        // Create user with multiple roles
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        
        Set<Role> roles = new LinkedHashSet<>(); // LinkedHashSet to maintain order
        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName(Role.ERole.ROLE_USER);
        roles.add(userRole);
        
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName(Role.ERole.ROLE_ADMIN);
        roles.add(adminRole);
        
        Role moderatorRole = new Role();
        moderatorRole.setId(3);
        moderatorRole.setName(Role.ERole.ROLE_MODERATOR);
        roles.add(moderatorRole);
        
        user.setRoles(roles);
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        
        // Verify all roles are present
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(3, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR")));
    }

    @Test
    void testEqualsWithNullId() {
        UserDetailsImpl user1 = new UserDetailsImpl(null, "testuser", "test@example.com", "password", List.of());
        UserDetailsImpl user2 = new UserDetailsImpl(null, "different", "different@example.com", "different", List.of());
        UserDetailsImpl user3 = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", List.of());
        
        // Both null IDs should be equal
        assertEquals(user1, user2);
        
        // Null ID not equal to non-null ID
        assertNotEquals(user1, user3);
        assertNotEquals(user3, user1);
    }
}
