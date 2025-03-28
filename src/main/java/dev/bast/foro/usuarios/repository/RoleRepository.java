package dev.bast.foro.usuarios.repository;

import dev.bast.foro.usuarios.model.Role;
import dev.bast.foro.usuarios.model.Role.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(ERole name);
}