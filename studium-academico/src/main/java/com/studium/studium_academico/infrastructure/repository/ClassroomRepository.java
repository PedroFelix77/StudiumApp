package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    Optional<Classroom> findByClassEntityIdAndDisciplineId(
            UUID classId,
            UUID disciplineId
    );

    List<Classroom> findByTeacher_Id(UUID teacherId);

    @Query("SELECT c FROM Classroom c " +
            "JOIN FETCH c.classEntity cl " +
            "JOIN FETCH c.discipline d " +
            "WHERE c.teacher.id = :teacherId " +
            "AND cl.id = :classId " +
            "ORDER BY d.name")
    List<Classroom> findByTeacherIdAndClassEntityId(
            @Param("teacherId") UUID teacherId,
            @Param("classId") UUID classId
    );

    List<Classroom> findByTeacher_IdAndClassEntity_Id(UUID teacherId, UUID classId);

    // 2. Buscar aula específica de um professor em uma turma e disciplina
    Optional<Classroom> findByTeacher_IdAndClassEntity_IdAndDiscipline_Id(
            UUID teacherId,
            UUID classId,
            UUID disciplineId
    );

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Classroom c " +
            "WHERE c.teacher.id = :teacherId " +
            "AND c.classEntity.id = :classId " +
            "AND c.discipline.id = :disciplineId")
    boolean existsByTeacherAndClassAndDiscipline(
            @Param("teacherId") UUID teacherId,
            @Param("classId") UUID classId,
            @Param("disciplineId") UUID disciplineId
    );
}
