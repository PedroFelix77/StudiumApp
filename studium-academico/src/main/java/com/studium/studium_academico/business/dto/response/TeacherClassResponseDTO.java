package com.studium.studium_academico.business.dto.response;

import java.util.UUID;

public record TeacherClassResponseDTO(
        UUID id,
        UUID teacherId,
        String teacherName,
        UUID classId,
        String className,
        UUID courseId,
        String courseName,
        UUID disciplineId,
        String disciplineName,
        Integer weeklyHours,
        Boolean isMainTeacher
) {}

