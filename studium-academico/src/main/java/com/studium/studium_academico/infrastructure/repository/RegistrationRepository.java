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

    // Buscar matrícula por número
    Optional<Registration> findByRegistrationNumber(String registrationNumber);

    // Verificar se aluno já está matriculado na turma
    @Query("SELECT COUNT(r) > 0 FROM Registration r WHERE r.student.id = :studentId AND r.classEntity.id = :classId")
    boolean existsByStudentIdAndClassEntityId(@Param("studentId") UUID studentId, @Param("classId") UUID classId);

    // Contar total de matrículas (para gerar sequencial)
    long count();

    // Contar matrículas por aluno
    Long countByStudentId(UUID studentId);

    // Contar matrículas por turma
    Long countByClassEntityId(UUID classId);

    @Query("""
    SELECT COUNT(r)
    FROM Registration r
    WHERE r.course.id = :courseId
""")
    long countByCourseId(UUID courseId);

    @Query("""
        SELECT COUNT(DISTINCT r.student.id)
        FROM Registration r
        JOIN r.classEntity ce
        JOIN TeacherClass tc ON tc.classEntity.id = ce.id
        WHERE tc.teacher.id = :teacherId
    """)
    long countByTeacherId(@Param("teacherId") UUID teacherId);

    @Query("SELECT r FROM Registration r " +
            "JOIN FETCH r.student s " +
            "JOIN FETCH s.user u " +
            "WHERE r.classEntity.id = :classId " +
            "ORDER BY u.name")
    List<Registration> findByClassEntityId(@Param("classId") UUID classId);

}
