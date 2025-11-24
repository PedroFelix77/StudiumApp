package com.studium.studium_academico.business.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record TeacherResponseDTO(
        UUID id,
        UserResponseDTO user,
        DepartmentResponseDTO department,
        List<CourseResponseDTO> courses,
        List<DisciplineResponseDTO> disciplines,
        LocalDate hireDate,
        String specialty,
        List<TeacherClassResponseDTO> teacherClasses
) {
}
