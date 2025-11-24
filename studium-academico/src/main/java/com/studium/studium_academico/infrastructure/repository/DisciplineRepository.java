package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Course;
import com.studium.studium_academico.infrastructure.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, UUID> {
    Optional<Discipline> findByCode(String code);
    List<Discipline> findByNameContainingIgnoreCase(String name);
    List<Discipline> findByCourse(Course course);
    List<Discipline> findByCourseId(UUID courseId);
    boolean existsByCode(String code);

    // Buscar disciplinas por professor
    List<Discipline> findByTeacherId(UUID teacherId);

    // Buscar disciplinas sem professor atribuído
    List<Discipline> findByTeacherIsNull();

    // Buscar disciplinas por professor e curso
    List<Discipline> findByTeacherIdAndCourseId(UUID teacherId, UUID courseId);
}
