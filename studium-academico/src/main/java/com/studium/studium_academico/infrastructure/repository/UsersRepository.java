package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.UserRole;
import com.studium.studium_academico.infrastructure.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByCpf(String cpf);
    List<Users> findByRole(UserRole role);

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);

}
