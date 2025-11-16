package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByInstitutionId(UUID institutionId);
    boolean existsByNameAndInstitutionId(String name, UUID institutionId);

    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.name = :name AND d.institution.id = :institutionId AND d.id != d.id")
    boolean existsByNameAndInstitutionIdAndIdNot(
            @Param("name") String name,
            @Param("institutionId") UUID institutionId,
            @Param("id") UUID id
    );

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.teachers LEFT JOIN FETCH d.courses WHERE d.id = :id")
    Optional<Department> findByIdWithTeachersAndCourses(@Param("id") UUID id);

}
