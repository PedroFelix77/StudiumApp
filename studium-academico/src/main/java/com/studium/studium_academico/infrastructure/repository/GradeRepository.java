package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Discipline;
import com.studium.studium_academico.infrastructure.entity.Grade;
import com.studium.studium_academico.infrastructure.entity.Registration;
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

    // Verifica duplicidade de nota
    boolean existsByRegistrationAndDisciplineAndTypeGrade(Registration registration, Discipline discipline, TypeGrade typeGrade);

    // Notas por curso (courseId) - via registration
    @Query("SELECT g FROM Grade g WHERE g.registration.course.id = :courseId")
    List<Grade> findByCourseId(@Param("courseId") UUID courseId);

    List<Grade> findByClassEntityIdAndDisciplineId(UUID classId, UUID disciplineId);

    // Busca por turma e disciplina (paginado)
    Page<Grade> findByClassEntityIdAndDisciplineId(UUID classId, UUID disciplineId, Pageable pageable);

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

    // Busca por curso, turma e disciplina
    @Query("SELECT g FROM Grade g " +
            "JOIN g.registration r " +
            "JOIN r.course c " +
            "WHERE c.id = :courseId " +
            "AND g.classEntity.id = :classId " +
            "AND g.discipline.id = :disciplineId")
    Page<Grade> findByCourseIdAndClassIdAndDisciplineId(
            @Param("courseId") UUID courseId,
            @Param("classId") UUID classId,
            @Param("disciplineId") UUID disciplineId,
            Pageable pageable
    );

    // Busca por curso (via matrícula -> curso)
    @Query("SELECT g FROM Grade g " +
            "JOIN g.registration r " +
            "JOIN r.course c " +
            "WHERE c.id = :courseId")
    Page<Grade> findByCourseId(@Param("courseId") UUID courseId, Pageable pageable);

    @Query("SELECT g, r, s, u " +
            "FROM Grade g " +
            "JOIN FETCH g.registration r " +
            "JOIN FETCH r.student s " +
            "JOIN FETCH s.user u " +
            "WHERE g.classEntity.id = :classId " +
            "AND g.discipline.id = :disciplineId")
    List<Object[]> findGradesWithStudentsByClassAndDiscipline(
            @Param("classId") UUID classId,
            @Param("disciplineId") UUID disciplineId
    );

}