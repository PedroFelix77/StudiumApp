package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Course;
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
public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByCodeCourse(String codeCourse);

    boolean existsByCodeCourse(String codeCourse);

    @Query("""
        SELECT c FROM Course c
        WHERE (:codeCourse IS NULL OR c.codeCourse LIKE %:codeCourse%)
    """)
    Page<Course> searchByCode(String codeCourse, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Course c " +
            "JOIN c.disciplines d " +
            "WHERE c.id = :courseId AND d.id = :disciplineId")
    boolean existsByIdAndDisciplinesId(
            @Param("courseId") UUID courseId,
            @Param("disciplineId") UUID disciplineId
    );

    @Query("""
    SELECT c
    FROM Course c
    JOIN c.teachers t
    WHERE t.id = :teacherId
""")
    List<Course> findByTeacherId(UUID teacherId);

    @Query("""
    select distinct c.course
    from TeacherClass tc
    join tc.classEntity c
    where tc.teacher.id = :teacherId
""")
    List<Course> findCoursesByTeacher(UUID teacherId);

}

