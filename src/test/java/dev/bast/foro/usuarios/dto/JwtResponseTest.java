package dev.bast.foro.usuarios.dto;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JwtResponseTest {

    @Test
    void testNoArgsConstructor() {
        JwtResponse response = new JwtResponse();
        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Bearer", response.getType()); // default value
        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertNull(response.getRoles());
    }

    @Test
    void testAllArgsConstructor() {
        List<String> roles = Arrays.asList("ROLE_USER", "ROLE_ADMIN");
        JwtResponse response = new JwtResponse("token123", "Bearer", 1L, "testuser", "test@example.com", roles);
        
        assertEquals("token123", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(roles, response.getRoles());
    }

    @Test
    void testFiveArgsConstructor() {
        List<String> roles = Arrays.asList("ROLE_USER");
        JwtResponse response = new JwtResponse("token456", 2L, "user2", "user2@example.com", roles);
        
        assertEquals("token456", response.getToken());
        assertEquals("Bearer", response.getType()); // default value
        assertEquals(2L, response.getId());
        assertEquals("user2", response.getUsername());
        assertEquals("user2@example.com", response.getEmail());
        assertEquals(roles, response.getRoles());
    }

    @Test
    void testSetBasedConstructor() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_MODERATOR");
        
        JwtResponse response = new JwtResponse("token789", 3L, "user3", "user3@example.com", roles);
        
        assertEquals("token789", response.getToken());
        assertEquals("Bearer", response.getType()); // default value
        assertEquals(3L, response.getId());
        assertEquals("user3", response.getUsername());
        assertEquals("user3@example.com", response.getEmail());
        
        // Roles should be converted from Set to List
        assertNotNull(response.getRoles());
        assertEquals(2, response.getRoles().size());
        assertTrue(response.getRoles().contains("ROLE_USER"));
        assertTrue(response.getRoles().contains("ROLE_MODERATOR"));
    }

    @Test
    void testSettersAndGetters() {
        JwtResponse response = new JwtResponse();
        List<String> roles = Arrays.asList("ROLE_USER");
        
        response.setToken("mytoken");
        response.setType("Custom");
        response.setId(5L);
        response.setUsername("myuser");
        response.setEmail("myuser@example.com");
        response.setRoles(roles);
        
        assertEquals("mytoken", response.getToken());
        assertEquals("Custom", response.getType());
        assertEquals(5L, response.getId());
        assertEquals("myuser", response.getUsername());
        assertEquals("myuser@example.com", response.getEmail());
        assertEquals(roles, response.getRoles());
    }

    @Test
    void testDefaultTypeValue() {
        JwtResponse response = new JwtResponse();
        assertEquals("Bearer", response.getType());
        
        // Test all constructors maintain the default
        List<String> roles = Arrays.asList("ROLE_USER");
        JwtResponse response2 = new JwtResponse("token", 1L, "user", "email", roles);
        assertEquals("Bearer", response2.getType());
        
        Set<String> roleSet = new HashSet<>(roles);
        JwtResponse response3 = new JwtResponse("token", 1L, "user", "email", roleSet);
        assertEquals("Bearer", response3.getType());
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> roles = Arrays.asList("ROLE_USER");
        JwtResponse response1 = new JwtResponse("token", "Bearer", 1L, "user", "email", roles);
        JwtResponse response2 = new JwtResponse("token", "Bearer", 1L, "user", "email", roles);
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testNotEquals() {
        List<String> roles = Arrays.asList("ROLE_USER");
        JwtResponse response1 = new JwtResponse("token1", "Bearer", 1L, "user1", "email1", roles);
        JwtResponse response2 = new JwtResponse("token2", "Bearer", 2L, "user2", "email2", roles);
        
        assertNotEquals(response1, response2);
    }

    @Test
    void testToString() {
        List<String> roles = Arrays.asList("ROLE_USER");
        JwtResponse response = new JwtResponse("token123", "Bearer", 1L, "testuser", "test@example.com", roles);
        
        String toString = response.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("JwtResponse"));
        assertTrue(toString.contains("token=token123"));
        assertTrue(toString.contains("type=Bearer"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("roles="));
    }

    @Test
    void testRolesConversionFromSet() {
        Set<String> roleSet = new LinkedHashSet<>(); // Preserve order
        roleSet.add("ROLE_ADMIN");
        roleSet.add("ROLE_USER");
        roleSet.add("ROLE_MODERATOR");
        
        JwtResponse response = new JwtResponse("token", 1L, "user", "email", roleSet);
        
        assertNotNull(response.getRoles());
        assertEquals(3, response.getRoles().size());
        
        // Verify all roles are present
        assertTrue(response.getRoles().contains("ROLE_ADMIN"));
        assertTrue(response.getRoles().contains("ROLE_USER"));
        assertTrue(response.getRoles().contains("ROLE_MODERATOR"));
    }

    @Test
    void testEmptyRoles() {
        // Test with empty List
        List<String> emptyList = new ArrayList<>();
        JwtResponse response1 = new JwtResponse("token", 1L, "user", "email", emptyList);
        assertNotNull(response1.getRoles());
        assertTrue(response1.getRoles().isEmpty());
        
        // Test with empty Set
        Set<String> emptySet = new HashSet<>();
        JwtResponse response2 = new JwtResponse("token", 1L, "user", "email", emptySet);
        assertNotNull(response2.getRoles());
        assertTrue(response2.getRoles().isEmpty());
    }

    @Test
    void testNullRoles() {
        // Test what happens when roles are explicitly set to null
        JwtResponse response = new JwtResponse();
        response.setRoles(null);
        assertNull(response.getRoles());
    }

    @Test
    void testImmutabilityOfRolesFromSet() {
        Set<String> roleSet = new HashSet<>();
        roleSet.add("ROLE_USER");
        
        JwtResponse response = new JwtResponse("token", 1L, "user", "email", roleSet);
        
        // Modify original set
        roleSet.add("ROLE_ADMIN");
        
        // Response roles should not be affected
        assertEquals(1, response.getRoles().size());
        assertTrue(response.getRoles().contains("ROLE_USER"));
        assertFalse(response.getRoles().contains("ROLE_ADMIN"));
    }

    @Test
    void testConstructorWithNullValues() {
        JwtResponse response = new JwtResponse(null, null, null, null, null, null);
        
        assertNull(response.getToken());
        assertNull(response.getType());
        assertNull(response.getId());
        assertNull(response.getUsername());
        assertNull(response.getEmail());
        assertNull(response.getRoles());
    }

    @Test
    void testListCopyOf() {
        // Test that List.copyOf() is used correctly in the Set constructor
        Set<String> roleSet = new HashSet<>();
        roleSet.add("ROLE_USER");
        roleSet.add("ROLE_ADMIN");
        
        JwtResponse response = new JwtResponse("token", 1L, "user", "email", roleSet);
        
        // Try to modify the returned list (should be immutable if List.copyOf() is used)
        List<String> roles = response.getRoles();
        assertThrows(UnsupportedOperationException.class, () -> {
            roles.add("ROLE_MODERATOR");
        });
    }
}
