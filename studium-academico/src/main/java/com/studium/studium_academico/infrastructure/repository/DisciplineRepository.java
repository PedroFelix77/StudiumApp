package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Course;
import com.studium.studium_academico.infrastructure.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, UUID> {
    Optional<Discipline> findByCode(String code);
    List<Discipline> findByNameContainingIgnoreCase(String name);
    List<Discipline> findByCourse(Course course);
    boolean existsByCode(String code);

    // Buscar disciplinas por professor
    List<Discipline> findByTeacherId(UUID teacherId);

    @Query("SELECT DISTINCT d FROM Discipline d " +
            "JOIN d.course c " +
            "WHERE c.id = :courseId " +
            "ORDER BY d.name")
    List<Discipline> findByCourseId(@Param("courseId") UUID courseId);
    // Buscar disciplinas sem professor atribuído
    List<Discipline> findByTeacherIsNull();

    // Buscar disciplinas por professor e curso
    List<Discipline> findByTeacherIdAndCourseId(UUID teacherId, UUID courseId);
}
