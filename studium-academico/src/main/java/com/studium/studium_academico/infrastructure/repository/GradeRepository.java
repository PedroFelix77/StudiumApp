package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Grade;
import com.studium.studium_academico.infrastructure.entity.Registration;
import com.studium.studium_academico.infrastructure.entity.TypeGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID>, JpaSpecificationExecutor<Grade> {
    // Encontrar notas de um aluno por disciplina/turma
    @Query("SELECT g FROM Grade g WHERE g.registration.id = :registrationId")
    List<Grade> findByRegistrationId(@Param("registrationId") UUID registrationId);

    Long countByRegistrationStudentIdAndGradeIsNull(UUID studentId);
    @Query("""
    SELECT AVG(g.grade)
    FROM Grade g
    WHERE g.registration.course.id = :courseId
""")
    Optional<Double> findAverageGradeByCourseId(UUID courseId);

    @Query("""
    SELECT AVG(g.grade)
    FROM Grade g
    WHERE g.registration.course.id = :courseId
      AND g.registration.student.id = :studentId
""")
    Optional<Double> findAverageGradeByCourseIdAndStudentId(UUID courseId, UUID studentId);

    @Query("""
    SELECT AVG(g.grade)
    FROM Grade g
    WHERE g.registration.course.id = :courseId
      AND g.teacher.id = :teacherId
""")
    Optional<Double> findAverageGradeByCourseIdAndTeacherId(UUID courseId, UUID teacherId);




    // Buscar por registro + tipo de prova
    Optional<Grade> findByRegistrationStudentIdAndTypeGrade(UUID registrationId, TypeGrade typeGrade);

    // Contagens/estatísticas simples
    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.discipline.id = :disciplineId")
    BigDecimal averageByDiscipline(@Param("disciplineId") UUID disciplineId);

    List<Grade> findByRecordedByTeacherId(UUID teacherId);

    List<Registration> findByStudentId(UUID studentId);
    List<Registration> findByClassEntityId(UUID classId);
    List<Registration> findByCourseId(UUID courseId);
    Optional<Registration> findByRegistrationNumber(String registrationNumber);
}
