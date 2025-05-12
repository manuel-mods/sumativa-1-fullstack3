package dev.bast.foro.usuarios.repository;

import dev.bast.foro.usuarios.model.User;
import dev.bast.foro.usuarios.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    // @Autowired
    // private UserRepository userRepository;
    
    // @Autowired
    // private TestEntityManager entityManager;

    // private User user1;
    // private User user2;

    // @BeforeEach
    // void setUp() {
    //     userRepository.deleteAll();
    //     entityManager.flush();
        
    //     Set<Role> roles = new HashSet<>();
        
    //     user1 = new User();
    //     user1.setUsername("testuser1");
    //     user1.setEmail("test1@example.com");
    //     user1.setPassword("password123");
    //     user1.setRoles(roles);
    //     user1.setActive(true);
    //     user1.setCreatedAt(LocalDateTime.now());
    //     user1.setUpdatedAt(LocalDateTime.now());
        
    //     user2 = new User();
    //     user2.setUsername("testuser2");
    //     user2.setEmail("test2@example.com");
    //     user2.setPassword("password456");
    //     user2.setRoles(roles);
    //     user2.setActive(false);
    //     user2.setCreatedAt(LocalDateTime.now());
    //     user2.setUpdatedAt(LocalDateTime.now());
    // }

    // @Test
    // void testSaveUser() {
    //     User savedUser = userRepository.save(user1);
        
    //     assertNotNull(savedUser.getId());
    //     assertEquals("testuser1", savedUser.getUsername());
    //     assertEquals("test1@example.com", savedUser.getEmail());
    //     assertEquals("password123", savedUser.getPassword());
    //     assertTrue(savedUser.isActive());
    // }

    // @Test
    // void testFindByUsername() {
    //     userRepository.save(user1);
        
    //     Optional<User> foundUser = userRepository.findByUsername("testuser1");
        
    //     assertTrue(foundUser.isPresent());
    //     assertEquals("testuser1", foundUser.get().getUsername());
    //     assertEquals("test1@example.com", foundUser.get().getEmail());
    // }

    // @Test
    // void testFindByUsernameNotFound() {
    //     Optional<User> foundUser = userRepository.findByUsername("nonexistent");
    //     assertFalse(foundUser.isPresent());
    // }

    // @Test
    // void testExistsByUsername() {
    //     userRepository.save(user1);
        
    //     assertTrue(userRepository.existsByUsername("testuser1"));
    //     assertFalse(userRepository.existsByUsername("nonexistent"));
    // }

    // @Test
    // void testExistsByEmail() {
    //     userRepository.save(user1);
        
    //     assertTrue(userRepository.existsByEmail("test1@example.com"));
    //     assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    // }

    // @Test
    // void testFindById() {
    //     User savedUser = userRepository.save(user1);
        
    //     Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
    //     assertTrue(foundUser.isPresent());
    //     assertEquals(savedUser.getId(), foundUser.get().getId());
    //     assertEquals("testuser1", foundUser.get().getUsername());
    // }

    // @Test
    // void testFindByIdNotFound() {
    //     Optional<User> foundUser = userRepository.findById(999L);
    //     assertFalse(foundUser.isPresent());
    // }

    // @Test
    // void testUpdateUser() {
    //     User savedUser = userRepository.save(user1);
        
    //     savedUser.setEmail("updated@example.com");
    //     savedUser.setActive(false);
    //     User updatedUser = userRepository.save(savedUser);
        
    //     assertEquals(savedUser.getId(), updatedUser.getId());
    //     assertEquals("updated@example.com", updatedUser.getEmail());
    //     assertFalse(updatedUser.isActive());
    // }

    // @Test
    // void testDeleteUser() {
    //     User savedUser = userRepository.save(user1);
    //     Long userId = savedUser.getId();
        
    //     userRepository.deleteById(userId);
        
    //     Optional<User> deletedUser = userRepository.findById(userId);
    //     assertFalse(deletedUser.isPresent());
    // }

    // @Test
    // void testCount() {
    //     assertEquals(0, userRepository.count());
        
    //     userRepository.save(user1);
    //     userRepository.save(user2);
        
    //     assertEquals(2, userRepository.count());
    // }

    // @Test
    // void testDeleteAll() {
    //     userRepository.save(user1);
    //     userRepository.save(user2);
    //     assertEquals(2, userRepository.count());
        
    //     userRepository.deleteAll();
        
    //     assertEquals(0, userRepository.count());
    // }

    // @Test
    // void testExistsById() {
    //     User savedUser = userRepository.save(user1);
        
    //     assertTrue(userRepository.existsById(savedUser.getId()));
    //     assertFalse(userRepository.existsById(999L));
    // }

    // @Test
    // void testCaseSensitivity() {
    //     userRepository.save(user1);
        
    //     // Test username case sensitivity
    //     Optional<User> upperCaseUser = userRepository.findByUsername("TESTUSER1");
    //     assertFalse(upperCaseUser.isPresent());
        
    //     // Test email case sensitivity
    //     assertFalse(userRepository.existsByEmail("TEST1@EXAMPLE.COM"));
    // }

    // @Test
    // void testMultipleUsersWithDifferentEmails() {
    //     userRepository.save(user1);
    //     userRepository.save(user2);
        
    //     assertTrue(userRepository.existsByEmail("test1@example.com"));
    //     assertTrue(userRepository.existsByEmail("test2@example.com"));
    //     assertFalse(userRepository.existsByEmail("test3@example.com"));
    // }

    // @Test
    // void testMultipleUsersWithDifferentUsernames() {
    //     userRepository.save(user1);
    //     userRepository.save(user2);
        
    //     assertTrue(userRepository.existsByUsername("testuser1"));
    //     assertTrue(userRepository.existsByUsername("testuser2"));
    //     assertFalse(userRepository.existsByUsername("testuser3"));
    // }

    // @Test
    // void testTimestamps() {
    //     LocalDateTime before = LocalDateTime.now();
    //     User savedUser = userRepository.save(user1);
    //     LocalDateTime after = LocalDateTime.now();
        
    //     assertNotNull(savedUser.getCreatedAt());
    //     assertNotNull(savedUser.getUpdatedAt());
        
    //     // The timestamps should be between before and after
    //     assertTrue(savedUser.getCreatedAt().isAfter(before.minusSeconds(1)));
    //     assertTrue(savedUser.getCreatedAt().isBefore(after.plusSeconds(1)));
    // }

    // @Test
    // void testRepositoryIsInterface() {
    //     assertTrue(UserRepository.class.isInterface());
    //     assertTrue(userRepository instanceof UserRepository);
    // }

    // @Test
    // void testFindAll() {
    //     userRepository.save(user1);
    //     userRepository.save(user2);
        
    //     assertEquals(2, userRepository.findAll().size());
    // }

    // @Test
    // void testBooleanQueriesWithNoData() {
    //     assertFalse(userRepository.existsByUsername("anyuser"));
    //     assertFalse(userRepository.existsByEmail("any@email.com"));
    // }

    // @Test
    // void testOptionalQueriesWithNoData() {
    //     Optional<User> noUser = userRepository.findByUsername("anyuser");
    //     assertFalse(noUser.isPresent());
    // }
}
