package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.business.dto.response.TeacherResponseDTO;
import com.studium.studium_academico.infrastructure.entity.TeacherClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeacherClassRepository extends JpaRepository<TeacherClass, UUID> {
    List<TeacherClass> findByTeacherId(UUID teacherId);
}
