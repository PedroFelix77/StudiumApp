package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Discipline;
import com.studium.studium_academico.infrastructure.entity.Registration;
import com.studium.studium_academico.infrastructure.entity.StudentDisciplineResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StudentDisciplineResultRepository extends JpaRepository<StudentDisciplineResult, UUID> {
    @Query("SELECT s FROM StudentDisciplineResult s WHERE s.registration = :registration AND s.discipline = :discipline")
    Optional<StudentDisciplineResult> findByRegistrationAndDiscipline(
            @Param("registration") Registration registration,
            @Param("discipline") Discipline discipline
    );
}
