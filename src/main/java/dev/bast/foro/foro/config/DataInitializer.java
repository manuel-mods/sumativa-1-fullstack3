package dev.bast.foro.foro.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.bast.foro.foros.model.Comment;
import dev.bast.foro.foros.model.Topic;
import dev.bast.foro.foros.repository.CommentRepository;
import dev.bast.foro.foros.repository.TopicRepository;
import dev.bast.foro.usuarios.model.Role;
import dev.bast.foro.usuarios.model.User;
import dev.bast.foro.usuarios.repository.RoleRepository;
import dev.bast.foro.usuarios.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            TopicRepository topicRepository,
            CommentRepository commentRepository,
            PasswordEncoder encoder
    ) {
        return args -> {
            log.info("Initializing database with sample data...");

            // Create roles if they don't exist
            if (roleRepository.count() == 0) {
                log.info("Creating roles...");
                roleRepository.save(new Role(Role.ERole.ROLE_USER));
                roleRepository.save(new Role(Role.ERole.ROLE_MODERATOR));
                roleRepository.save(new Role(Role.ERole.ROLE_ADMIN));
            }

            // Get roles
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role USER not found."));
            Role modRole = roleRepository.findByName(Role.ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role MODERATOR not found."));
            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found."));

            // Create users if they don't exist
            if (userRepository.count() == 0) {
                log.info("Creating users...");

                // Admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("admin123"));
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(userRole);
                adminRoles.add(modRole);
                adminRoles.add(adminRole);
                admin.setRoles(adminRoles);
                userRepository.save(admin);

                // Moderator user
                User moderator = new User();
                moderator.setUsername("moderator");
                moderator.setEmail("moderator@example.com");
                moderator.setPassword(encoder.encode("mod123"));
                Set<Role> modRoles = new HashSet<>();
                modRoles.add(userRole);
                modRoles.add(modRole);
                moderator.setRoles(modRoles);
                userRepository.save(moderator);

                // Regular user
                User user = new User();
                user.setUsername("user");
                user.setEmail("user@example.com");
                user.setPassword(encoder.encode("user123"));
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(userRole);
                user.setRoles(userRoles);
                userRepository.save(user);
            }

            // Create topics if they don't exist
            if (topicRepository.count() == 0) {
                log.info("Creating topics...");

                User admin = userRepository.findByUsername("admin").orElseThrow();
                User moderator = userRepository.findByUsername("moderator").orElseThrow();
                User user = userRepository.findByUsername("user").orElseThrow();

                // Topic 1
                Topic topic1 = new Topic();
                topic1.setTitle("Welcome to our Forum");
                topic1.setContent("This is the official welcome topic for our new forum. Feel free to introduce yourself here!");
                topic1.setUserId(admin.getId());
                topic1.setUsername(admin.getUsername());
                topicRepository.save(topic1);

                // Topic 2
                Topic topic2 = new Topic();
                topic2.setTitle("Forum Rules and Guidelines");
                topic2.setContent("Please read and follow these rules to ensure a respectful and productive community.");
                topic2.setUserId(moderator.getId());
                topic2.setUsername(moderator.getUsername());
                topicRepository.save(topic2);

                // Topic 3
                Topic topic3 = new Topic();
                topic3.setTitle("Introductions Thread");
                topic3.setContent("Hi everyone! I'm new here. Looking forward to participating in discussions.");
                topic3.setUserId(user.getId());
                topic3.setUsername(user.getUsername());
                topicRepository.save(topic3);

                // Create comments
                if (commentRepository.count() == 0) {
                    log.info("Creating comments...");

                    // Comments for Topic 1
                    Comment comment1 = new Comment();
                    comment1.setContent("Welcome everyone! Happy to have you all here.");
                    comment1.setTopicId(topic1.getId());
                    comment1.setUserId(moderator.getId());
                    comment1.setUsername(moderator.getUsername());
                    commentRepository.save(comment1);

                    Comment comment2 = new Comment();
                    comment2.setContent("Thanks for setting up this forum. Looking forward to the discussions!");
                    comment2.setTopicId(topic1.getId());
                    comment2.setUserId(user.getId());
                    comment2.setUsername(user.getUsername());
                    commentRepository.save(comment2);

                    // Comments for Topic 2
                    Comment comment3 = new Comment();
                    comment3.setContent("These rules make a lot of sense. Thanks for creating clear guidelines.");
                    comment3.setTopicId(topic2.getId());
                    comment3.setUserId(user.getId());
                    comment3.setUsername(user.getUsername());
                    commentRepository.save(comment3);

                    // Comments for Topic 3
                    Comment comment4 = new Comment();
                    comment4.setContent("Welcome to the forum! Feel free to look around and join the discussions.");
                    comment4.setTopicId(topic3.getId());
                    comment4.setUserId(admin.getId());
                    comment4.setUsername(admin.getUsername());
                    commentRepository.save(comment4);

                    Comment comment5 = new Comment();
                    comment5.setContent("Hello and welcome! Great to have you here.");
                    comment5.setTopicId(topic3.getId());
                    comment5.setUserId(moderator.getId());
                    comment5.setUsername(moderator.getUsername());
                    commentRepository.save(comment5);
                }
            }

            log.info("Database initialization complete!");
        };
    }
}