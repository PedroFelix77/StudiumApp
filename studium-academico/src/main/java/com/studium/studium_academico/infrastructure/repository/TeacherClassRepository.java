package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.business.dto.response.TeacherResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Course;
import com.studium.studium_academico.infrastructure.entity.TeacherClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherClassRepository extends JpaRepository<TeacherClass, UUID> {
    List<TeacherClass> findByTeacherId(UUID teacherId);
    List<TeacherClass> findByCourseId(UUID courseId);
    List<TeacherClass> findByTeacherIdAndCourseId(UUID teacherId, UUID courseId);
    boolean existsByTeacherIdAndClassEntityIdAndDisciplineId(
            UUID teacherId,
            UUID classId,
            UUID disciplineId
    );
    @Query("""
        SELECT DISTINCT c.course
        FROM TeacherClass tc
        JOIN tc.classEntity c
        WHERE tc.teacher.id = :teacherId
    """)
    List<Course> findCoursesByTeacher(@Param("teacherId") UUID teacherId);

    List<TeacherClass> findByClassEntityId(UUID classId);
    // MÉTODO NOVO: Buscar por disciplina
    List<TeacherClass> findByDisciplineId(UUID disciplineId);

}
