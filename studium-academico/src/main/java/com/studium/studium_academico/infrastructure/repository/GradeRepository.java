package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Grade;
import com.studium.studium_academico.infrastructure.entity.TypeGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Encontrar notas por registrationId
    @Query("SELECT g FROM Grade g WHERE g.registration.id = :registrationId")
    List<Grade> findByRegistrationId(@Param("registrationId") UUID registrationId);

    // Contar notas nulas por studentId (via registration)
    @Query("""
        SELECT COUNT(g) FROM Grade g WHERE g.registration.student.id = :studentId AND g.grade IS NULL
    """)
    Long countByStudentIdAndGradeIsNull(@Param("studentId") UUID studentId);

    // Média de notas por curso
    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.registration.course.id = :courseId")
    Optional<Double> findAverageGradeByCourseId(UUID courseId);

    // Média de notas por curso e aluno
    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.registration.course.id = :courseId AND g.registration.student.id = :studentId")
    Optional<Double> findAverageGradeByCourseIdAndStudentId(UUID courseId, UUID studentId);

    // Média de notas por curso e professor
    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.registration.course.id = :courseId AND g.teacher.id = :teacherId")
    Optional<Double> findAverageGradeByCourseIdAndTeacherId(UUID courseId, UUID teacherId);

    // Buscar por registrationId e tipo de prova
    @Query("SELECT g FROM Grade g WHERE g.registration.id = :registrationId AND g.typeGrade = :typeGrade")
    Optional<Grade> findByRegistrationIdAndTypeGrade(@Param("registrationId") UUID registrationId, @Param("typeGrade") TypeGrade typeGrade);

    // Média por disciplina
    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.discipline.id = :disciplineId")
    BigDecimal averageByDiscipline(@Param("disciplineId") UUID disciplineId);

    // Notas lançadas por um professor
    @Query("SELECT g FROM Grade g WHERE g.teacher.id = :teacherId")
    List<Grade> findByTeacherId(@Param("teacherId") UUID teacherId);

    // Notas por aluno (studentId) - via registration
    @Query("SELECT g FROM Grade g WHERE g.registration.student.id = :studentId")
    List<Grade> findByStudentId(@Param("studentId") UUID studentId);

    // Notas por turma (classId)
    @Query("SELECT g FROM Grade g WHERE g.classEntity.id = :classId")
    List<Grade> findByClassEntityId(@Param("classId") UUID classId);

    // Notas por curso (courseId) - via registration
    @Query("SELECT g FROM Grade g WHERE g.registration.course.id = :courseId")
    List<Grade> findByCourseId(@Param("courseId") UUID courseId);

    // Buscar por registrationNumber - via registration
    @Query("SELECT g FROM Grade g WHERE g.registration.registrationNumber = :registrationNumber")
    List<Grade> findByRegistrationNumber(@Param("registrationNumber") String registrationNumber);

    @Query("SELECT g FROM Grade g WHERE g.registration.id = :registrationId AND g.discipline.id = :disciplineId")
    List<Grade> findByRegistrationIdAndDisciplineId(
            @Param("registrationId") UUID registrationId,
            @Param("disciplineId") UUID disciplineId
    );

    @Query("""
    SELECT g
    FROM Grade g
    JOIN g.registration r
    JOIN r.classEntity c
    JOIN c.teacherClasses tc
    WHERE tc.teacher.id = :teacherId
""")
    Page<Grade> findGradesByTeacher(
            @Param("teacherId") UUID teacherId,
            Pageable pageable
    );

}