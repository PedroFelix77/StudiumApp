package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.EntityStatus;
import com.studium.studium_academico.infrastructure.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByUserId(UUID userId);
    Optional<Student> findByRegistration(String registration);
    Page<Student> findByStatus(EntityStatus status, Pageable pageable);

    @Query("""
        SELECT s FROM Student s
        JOIN s.user u
        WHERE (:q IS NULL OR :q = '' 
            OR LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(s.registration) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        """)
    Page<Student> searchByText(String q, Pageable pageable);

    Page<Student> findByUser_NameContainingIgnoreCaseOrUser_EmailContainingIgnoreCaseOrRegistrationContainingIgnoreCase(
            String name, String email, String registration, Pageable pageable
    );
}
