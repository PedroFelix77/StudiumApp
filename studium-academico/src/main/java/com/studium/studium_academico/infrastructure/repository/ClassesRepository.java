package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Classes;
import com.studium.studium_academico.infrastructure.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, UUID> {
    boolean existsByCodeClass(String codeClass);

    List<Classes> findByCourseId(UUID courseId);

    @Query("""
        SELECT DISTINCT c
        FROM Classes c
        JOIN c.teacherClasses tc
        WHERE tc.teacher.id = :teacherId
          AND c.course.id = :courseId
    """)
    List<Classes> findByTeacherAndCourse(
            @Param("teacherId") UUID teacherId,
            @Param("courseId") UUID courseId
    );


}
