package com.studium.studium_academico.business.dto.request;

import java.util.UUID;

public record TeacherClassCreateDTO(
        UUID teacherId,
        UUID classId,
        UUID courseId,
        UUID disciplineId,
        Integer weeklyHours,
        Boolean isMainTeacher
) {
}
