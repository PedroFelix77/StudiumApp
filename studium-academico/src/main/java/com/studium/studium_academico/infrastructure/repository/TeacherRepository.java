package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Department;
import com.studium.studium_academico.infrastructure.entity.EntityStatus;
import com.studium.studium_academico.infrastructure.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    Optional<Teacher> findByUserId(UUID userId);

    Page<Teacher> findByStatus(EntityStatus status, Pageable pageable);

    // Buscar professores por departamento
    List<Teacher> findByDepartment(Department department);
    List<Teacher> findByDepartmentId(UUID departmentId);

    @Query("""
        SELECT t FROM Teacher t
        JOIN t.user u
        WHERE (:q IS NULL OR :q = ''
            OR LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(t.specialty) LIKE LOWER(CONCAT('%', :q, '%'))
        )
        """)
    Page<Teacher> searchByText(String q, Pageable pageable);

    // Buscar professores por curso (através da tabela de junção course_teacher)
    @Query("SELECT t FROM Teacher t JOIN t.courses c WHERE c.id = :courseId")
    List<Teacher> findByCoursesId(@Param("courseId") UUID courseId);

    // Buscar professores por disciplina (através do relacionamento na Discipline)
    @Query("SELECT t FROM Teacher t JOIN t.disciplines d WHERE d.id = :disciplineId")
    List<Teacher> findByDisciplinesId(@Param("disciplineId") UUID disciplineId);

    // Buscar professores ativos
    @Query("SELECT t FROM Teacher t WHERE t.status = 'ACTIVE'")
    Long findActiveTeachers();

    // Buscar professores por especialidade
    List<Teacher> findBySpecialtyContainingIgnoreCase(String specialty);

    // Buscar professores com turmas em um determinado curso
    @Query("SELECT DISTINCT t FROM Teacher t JOIN t.teacherClasses tc WHERE tc.course.id = :courseId")
    List<Teacher> findTeachersWithClassesInCourse(@Param("courseId") UUID courseId);

    Page<Teacher> findByUser_NameContainingIgnoreCaseOrUser_EmailContainingIgnoreCaseOrSpecialtyContainingIgnoreCase(
            String name, String email, String specialty, Pageable pageable
    );
}
