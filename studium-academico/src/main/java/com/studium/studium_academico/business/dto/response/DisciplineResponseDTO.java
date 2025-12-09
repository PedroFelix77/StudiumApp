package com.studium.studium_academico.business.dto.response;

import java.util.List;
import java.util.UUID;

public record DisciplineResponseDTO(
        UUID id,
        String name,
        String code,
        Integer workload,
        CourseResponseDTO course,
        TeacherResponseDTO teacher,
        List<ClassResponseDTO> classes
) {}
