package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    boolean existsByRegistrationNumber(String registrationNumber);

    // Buscar matrículas por aluno
    List<Registration> findByStudentId(UUID studentId);

    // Buscar matrículas por curso
    List<Registration> findByCourseId(UUID courseId);

    // Buscar matrículas por turma
    List<Registration> findByClassEntityId(UUID classId);

    // Buscar matrícula por número
    Optional<Registration> findByRegistrationNumber(String registrationNumber);

    // Verificar se aluno já está matriculado na turma
    @Query("SELECT COUNT(r) > 0 FROM Registration r WHERE r.student.id = :studentId AND r.classEntity.id = :classId")
    boolean existsByStudentIdAndClassEntityId(@Param("studentId") UUID studentId, @Param("classId") UUID classId);

    // Contar total de matrículas (para gerar sequencial)
    long count();

    // Contar matrículas por aluno
    Long countByStudentId(UUID studentId);

    // Contar matrículas por curso
    Long countByCourseId(UUID courseId);

    // Contar matrículas por turma
    Long countByClassEntityId(UUID classId);
}
