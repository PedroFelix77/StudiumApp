package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    @Query("SELECT COUNT(c) FROM Classroom c " +
            "WHERE c.discipline.id = :disciplineId " +
            "AND c.date BETWEEN :startDate AND :endDate")
    Long countByDisciplineIdAndDateBetween(
            @Param("disciplineId") UUID disciplineId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
