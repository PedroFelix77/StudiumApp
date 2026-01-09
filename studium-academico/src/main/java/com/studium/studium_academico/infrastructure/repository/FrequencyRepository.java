package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Classroom;
import com.studium.studium_academico.infrastructure.entity.Frequency;
import com.studium.studium_academico.infrastructure.entity.Registration;
import com.studium.studium_academico.infrastructure.entity.StatusFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface FrequencyRepository extends JpaRepository<Frequency, UUID>, JpaSpecificationExecutor<Frequency> {

    boolean existsByRegistrationAndClassroomAndAttendanceDate(
            Registration registration, Classroom classroom, LocalDate date
    );

    @Query("""
    SELECT COUNT(f)
    FROM Frequency f
    WHERE f.registration.id = :registrationId
      AND f.statusFrequency = :status
""")
    Long countByRegistrationIdAndStatus(UUID registrationId, StatusFrequency status);

    @Query("SELECT f FROM Frequency f " +
            "JOIN f.registration r " +
            "JOIN f.classroom c " +
            "WHERE r.student.id = :studentId " +
            "AND c.discipline.id = :disciplineId " +
            "AND f.attendanceDate BETWEEN :startDate AND :endDate")
    List<Frequency> findByStudentIdAndDisciplineIdAndDateBetween(
            @Param("studentId") UUID studentId,
            @Param("disciplineId") UUID disciplineId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(f) " +
            "FROM Frequency f " +
            "WHERE f.registration.id = :registrationId " +
            "AND f.attendanceDate BETWEEN :start AND :end")
    Long countByRegistrationAndDateRange(UUID registrationId, LocalDate start, LocalDate end);

    @Query("SELECT COUNT(f) FROM Frequency f WHERE f.registration.id = :registrationId AND f.statusFrequency = :status AND f.attendanceDate BETWEEN :start AND :end")
    Long countByRegistrationAndStatusAndDateRange(UUID registrationId, StatusFrequency status, LocalDate start, LocalDate end);

    @Query("""
        SELECT COUNT(f) 
        FROM Frequency f 
        WHERE f.registration.student.id = :studentId 
        AND f.status = :status
    """)
    long countByStudentIdAndStatus(
            @Param("studentId") UUID studentId,
            @Param("status") StatusFrequency status
    );

}
