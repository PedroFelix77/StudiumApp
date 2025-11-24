package com.studium.studium_academico.mapper;

import com.studium.studium_academico.business.dto.response.GradeResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Grade;
import org.springframework.stereotype.Component;

@Component("gradeMapper")
public class GradeMapper{

    public GradeResponseDTO toResponseDTO(Grade grade) {
        if (grade == null) return null;

        return new GradeResponseDTO(
                grade.getId(),
                grade.getGrade(),
                grade.getTypeGrade(),
                grade.getRegistrationStudent() != null ? grade.getRegistrationStudent().getId() : null,
                grade.getRecordedByTeacher() != null ? grade.getRecordedByTeacher().getId() : null,
                grade.getDiscipline() != null ? grade.getDiscipline().getId() : null,
                grade.getClassGrade() != null ? grade.getClassGrade().getId() : null
        );
    }
}