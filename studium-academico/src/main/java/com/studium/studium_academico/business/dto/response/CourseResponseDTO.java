package com.studium.studium_academico.business.dto.response;

import java.util.List;
import java.util.UUID;

public record CourseResponseDTO(
        UUID id,
        String name,
        String code_course,
        DepartmentResponseDTO department,
        InstitutionResponseDTO institution,
        List<DisciplineResponseDTO> disciplines,
        List<ClassResponseDTO> classes,
        List<RegistrationResponseDTO> registrations,
        List<TeacherResponseDTO> teachers
) {
}
