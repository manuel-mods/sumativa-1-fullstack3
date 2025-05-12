package dev.bast.foro.usuarios.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testNoArgsConstructor() {
        Role role = new Role();
        assertNotNull(role);
        assertNull(role.getId());
        assertNull(role.getName());
    }

    @Test
    void testAllArgsConstructor() {
        Role role = new Role(1, Role.ERole.ROLE_USER);
        
        assertEquals(1, role.getId());
        assertEquals(Role.ERole.ROLE_USER, role.getName());
    }

    @Test
    void testSingleArgConstructor() {
        Role role = new Role(Role.ERole.ROLE_ADMIN);
        
        assertNull(role.getId());
        assertEquals(Role.ERole.ROLE_ADMIN, role.getName());
    }

    @Test
    void testSettersAndGetters() {
        Role role = new Role();
        
        role.setId(5);
        role.setName(Role.ERole.ROLE_MODERATOR);
        
        assertEquals(5, role.getId());
        assertEquals(Role.ERole.ROLE_MODERATOR, role.getName());
    }

    @Test
    void testERoleEnum() {
        // Test all enum values exist
        assertEquals(3, Role.ERole.values().length);
        
        assertEquals("ROLE_USER", Role.ERole.ROLE_USER.name());
        assertEquals("ROLE_MODERATOR", Role.ERole.ROLE_MODERATOR.name());
        assertEquals("ROLE_ADMIN", Role.ERole.ROLE_ADMIN.name());
    }

    @Test
    void testERoleValueOf() {
        assertEquals(Role.ERole.ROLE_USER, Role.ERole.valueOf("ROLE_USER"));
        assertEquals(Role.ERole.ROLE_MODERATOR, Role.ERole.valueOf("ROLE_MODERATOR"));
        assertEquals(Role.ERole.ROLE_ADMIN, Role.ERole.valueOf("ROLE_ADMIN"));
    }

    @Test
    void testERoleValueOfInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            Role.ERole.valueOf("INVALID_ROLE");
        });
    }

    @Test
    void testEqualsAndHashCode() {
        Role role1 = new Role();
        role1.setId(1);
        role1.setName(Role.ERole.ROLE_USER);
        
        Role role2 = new Role();
        role2.setId(1);
        role2.setName(Role.ERole.ROLE_USER);
        
        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testNotEquals() {
        Role role1 = new Role();
        role1.setId(1);
        role1.setName(Role.ERole.ROLE_USER);
        
        Role role2 = new Role();
        role2.setId(2);
        role2.setName(Role.ERole.ROLE_ADMIN);
        
        assertNotEquals(role1, role2);
    }

    @Test
    void testToString() {
        Role role = new Role();
        role.setId(1);
        role.setName(Role.ERole.ROLE_USER);
        
        String toString = role.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Role"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=ROLE_USER"));
    }

    @Test
    void testEntityAnnotations() {
        Class<Role> clazz = Role.class;
        
        assertTrue(clazz.isAnnotationPresent(jakarta.persistence.Entity.class));
        assertTrue(clazz.isAnnotationPresent(jakarta.persistence.Table.class));
        
        jakarta.persistence.Table tableAnnotation = clazz.getAnnotation(jakarta.persistence.Table.class);
        assertEquals("roles", tableAnnotation.name());
    }

    @Test
    void testFieldAnnotations() throws NoSuchFieldException {
        Class<Role> clazz = Role.class;
        
        assertTrue(clazz.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.Id.class));
        assertTrue(clazz.getDeclaredField("id").isAnnotationPresent(jakarta.persistence.GeneratedValue.class));
        
        jakarta.persistence.GeneratedValue genValue = clazz.getDeclaredField("id")
                .getAnnotation(jakarta.persistence.GeneratedValue.class);
        assertEquals(jakarta.persistence.GenerationType.IDENTITY, genValue.strategy());
        
        assertTrue(clazz.getDeclaredField("name").isAnnotationPresent(jakarta.persistence.Enumerated.class));
        assertTrue(clazz.getDeclaredField("name").isAnnotationPresent(jakarta.persistence.Column.class));
        
        jakarta.persistence.Enumerated enumerated = clazz.getDeclaredField("name")
                .getAnnotation(jakarta.persistence.Enumerated.class);
        assertEquals(jakarta.persistence.EnumType.STRING, enumerated.value());
        
        jakarta.persistence.Column nameColumn = clazz.getDeclaredField("name")
                .getAnnotation(jakarta.persistence.Column.class);
        assertEquals(20, nameColumn.length());
        assertTrue(nameColumn.unique());
    }

    @Test
    void testERoleOrdinal() {
        assertEquals(0, Role.ERole.ROLE_USER.ordinal());
        assertEquals(1, Role.ERole.ROLE_MODERATOR.ordinal());
        assertEquals(2, Role.ERole.ROLE_ADMIN.ordinal());
    }

    @Test
    void testConstructorsWithDifferentEnumValues() {
        Role userRole = new Role(Role.ERole.ROLE_USER);
        Role moderatorRole = new Role(Role.ERole.ROLE_MODERATOR);
        Role adminRole = new Role(Role.ERole.ROLE_ADMIN);
        
        assertEquals(Role.ERole.ROLE_USER, userRole.getName());
        assertEquals(Role.ERole.ROLE_MODERATOR, moderatorRole.getName());
        assertEquals(Role.ERole.ROLE_ADMIN, adminRole.getName());
    }

    // @Test
    // void testLombokDataAnnotation() {
    //     // Test that Lombok @Data is properly applied
    //     Class<Role> clazz = Role.class;
    //     assertTrue(clazz.isAnnotationPresent(lombok.Data.class));
    // }

    @Test
    void testNullName() {
        Role role = new Role();
        role.setId(1);
        role.setName(null);
        
        assertNull(role.getName());
    }

    @Test
    void testIdType() {
        Role role = new Role();
        role.setId(Integer.MAX_VALUE);
        
        assertEquals(Integer.MAX_VALUE, role.getId());
    }

    @Test
    void testCreateRoleWithEachEnumValue() {
        for (Role.ERole eRole : Role.ERole.values()) {
            Role role = new Role(eRole);
            assertEquals(eRole, role.getName());
            assertNull(role.getId());
        }
    }

    @Test
    void testHashCodeWithNullFields() {
        Role role1 = new Role();
        Role role2 = new Role();
        
        // Both have null id and name
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testEqualsWithNullFields() {
        Role role1 = new Role();
        Role role2 = new Role();
        
        // Both have null id and name
        assertEquals(role1, role2);
    }

    @Test
    void testEqualsWithPartialNullFields() {
        Role role1 = new Role();
        role1.setId(1);
        
        Role role2 = new Role();
        role2.setId(1);
        
        // Both have same id but null name
        assertEquals(role1, role2);
    }

    @Test
    void testNotEqualsWithDifferentName() {
        Role role1 = new Role(1, Role.ERole.ROLE_USER);
        Role role2 = new Role(1, Role.ERole.ROLE_ADMIN);
        
        assertNotEquals(role1, role2);
    }

    @Test
    void testEnumValuesCount() {
        // Ensure all expected roles are present
        Role.ERole[] values = Role.ERole.values();
        assertEquals(3, values.length);
        
        // Verify specific values exist
        boolean hasUser = false;
        boolean hasModerator = false;
        boolean hasAdmin = false;
        
        for (Role.ERole role : values) {
            if (role == Role.ERole.ROLE_USER) hasUser = true;
            if (role == Role.ERole.ROLE_MODERATOR) hasModerator = true;
            if (role == Role.ERole.ROLE_ADMIN) hasAdmin = true;
        }
        
        assertTrue(hasUser);
        assertTrue(hasModerator);
        assertTrue(hasAdmin);
    }
}
