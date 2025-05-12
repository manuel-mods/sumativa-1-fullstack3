package dev.bast.foro.usuarios.repository;

import dev.bast.foro.foro.ForoApplication;
import dev.bast.foro.usuarios.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;



@DataJpaTest
class RoleRepositoryTest {

    // @Autowired
    // private RoleRepository roleRepository;
    
    // @Autowired
    // private TestEntityManager entityManager;

    // @BeforeEach
    // void setUp() {
    //     roleRepository.deleteAll();
    //     entityManager.flush();
    // }

    // @Test
    // void testSaveRole() {
    //     Role role = new Role();
    //     role.setName(Role.ERole.ROLE_USER);
        
    //     Role savedRole = roleRepository.save(role);
        
    //     assertNotNull(savedRole.getId());
    //     assertEquals(Role.ERole.ROLE_USER, savedRole.getName());
    // }

    // @Test
    // void testFindByName() {
    //     Role role = new Role();
    //     role.setName(Role.ERole.ROLE_ADMIN);
    //     roleRepository.save(role);
        
    //     Optional<Role> foundRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN);
        
    //     assertTrue(foundRole.isPresent());
    //     assertEquals(Role.ERole.ROLE_ADMIN, foundRole.get().getName());
    // }

    // @Test
    // void testFindByNameNotFound() {
    //     Optional<Role> foundRole = roleRepository.findByName(Role.ERole.ROLE_USER);
        
    //     assertFalse(foundRole.isPresent());
    // }

    // @Test
    // void testSaveMultipleRoles() {
    //     Role userRole = new Role(Role.ERole.ROLE_USER);
    //     Role adminRole = new Role(Role.ERole.ROLE_ADMIN);
    //     Role moderatorRole = new Role(Role.ERole.ROLE_MODERATOR);
        
    //     roleRepository.save(userRole);
    //     roleRepository.save(adminRole);
    //     roleRepository.save(moderatorRole);
        
    //     assertEquals(3, roleRepository.count());
        
    //     assertTrue(roleRepository.findByName(Role.ERole.ROLE_USER).isPresent());
    //     assertTrue(roleRepository.findByName(Role.ERole.ROLE_ADMIN).isPresent());
    //     assertTrue(roleRepository.findByName(Role.ERole.ROLE_MODERATOR).isPresent());
    // }

    // @Test
    // void testFindById() {
    //     Role role = new Role(Role.ERole.ROLE_USER);
    //     Role savedRole = roleRepository.save(role);
        
    //     Optional<Role> foundRole = roleRepository.findById(savedRole.getId().longValue());
        
    //     assertTrue(foundRole.isPresent());
    //     assertEquals(savedRole.getId(), foundRole.get().getId());
    //     assertEquals(Role.ERole.ROLE_USER, foundRole.get().getName());
    // }

    // @Test
    // void testUpdateRole() {
    //     Role role = new Role(Role.ERole.ROLE_USER);
    //     Role savedRole = roleRepository.save(role);
        
    //     savedRole.setName(Role.ERole.ROLE_ADMIN);
    //     Role updatedRole = roleRepository.save(savedRole);
        
    //     assertEquals(savedRole.getId(), updatedRole.getId());
    //     assertEquals(Role.ERole.ROLE_ADMIN, updatedRole.getName());
    // }

    // @Test
    // void testDeleteRole() {
    //     Role role = new Role(Role.ERole.ROLE_USER);
    //     Role savedRole = roleRepository.save(role);
    //     Long roleId = savedRole.getId().longValue();
        
    //     roleRepository.deleteById(roleId);
        
    //     Optional<Role> deletedRole = roleRepository.findById(roleId);
    //     assertFalse(deletedRole.isPresent());
    // }

    // @Test
    // void testExistsById() {
    //     Role role = new Role(Role.ERole.ROLE_USER);
    //     Role savedRole = roleRepository.save(role);
        
    //     assertTrue(roleRepository.existsById(savedRole.getId().longValue()));
    //     assertFalse(roleRepository.existsById(999L));
    // }

    // @Test
    // void testDeleteAll() {
    //     roleRepository.save(new Role(Role.ERole.ROLE_USER));
    //     roleRepository.save(new Role(Role.ERole.ROLE_ADMIN));
    //     assertEquals(2, roleRepository.count());
        
    //     roleRepository.deleteAll();
        
    //     assertEquals(0, roleRepository.count());
    // }

    // @Test
    // void testUniqueConstraintOnName() {
    //     // Save first role
    //     Role role1 = new Role(Role.ERole.ROLE_USER);
    //     roleRepository.save(role1);
    //     entityManager.flush();
        
    //     // Try to save another role with the same name
    //     Role role2 = new Role(Role.ERole.ROLE_USER);
        
    //     // This should throw an exception due to unique constraint
    //     assertThrows(Exception.class, () -> {
    //         roleRepository.save(role2);
    //         entityManager.flush(); // Force the constraint check
    //     });
    // }

    // @Test
    // void testFindAll() {
    //     roleRepository.save(new Role(Role.ERole.ROLE_USER));
    //     roleRepository.save(new Role(Role.ERole.ROLE_ADMIN));
    //     roleRepository.save(new Role(Role.ERole.ROLE_MODERATOR));
        
    //     assertEquals(3, roleRepository.findAll().size());
    // }

    // @Test
    // void testRepositoryIsInterface() {
    //     assertTrue(RoleRepository.class.isInterface());
    //     assertTrue(roleRepository instanceof RoleRepository);
    // }

    // @Test
    // void testEmptyRepositoryOperations() {
    //     // Test operations on empty repository
    //     assertEquals(0, roleRepository.count());
    //     assertTrue(roleRepository.findAll().isEmpty());
    //     assertFalse(roleRepository.findByName(Role.ERole.ROLE_USER).isPresent());
    //     assertFalse(roleRepository.existsById(1L));
    // }

    // @Test
    // void testAllEnumValuesCanBeSaved() {
    //     for (Role.ERole eRole : Role.ERole.values()) {
    //         Role role = new Role(eRole);
    //         Role savedRole = roleRepository.save(role);
            
    //         assertNotNull(savedRole.getId());
    //         assertEquals(eRole, savedRole.getName());
            
    //         Optional<Role> foundRole = roleRepository.findByName(eRole);
    //         assertTrue(foundRole.isPresent());
    //         assertEquals(eRole, foundRole.get().getName());
    //     }
        
    //     assertEquals(Role.ERole.values().length, roleRepository.count());
    // }

    // @Test
    // void testCascadeOperations() {
    //     // Create and save a role
    //     Role role = new Role(Role.ERole.ROLE_USER);
    //     Role savedRole = roleRepository.save(role);
        
    //     // Since Role doesn't have cascade relationships, just verify basic operations
    //     assertNotNull(savedRole);
    //     assertEquals(1, roleRepository.count());
    // }
}
