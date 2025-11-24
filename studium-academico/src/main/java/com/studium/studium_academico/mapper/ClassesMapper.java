package com.studium.studium_academico.mapper;

import com.studium.studium_academico.business.dto.response.ClassResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Classes;
import org.springframework.stereotype.Component;

@Component("classesMapper")
public class ClassesMapper {

    public ClassResponseDTO toResponseDTO(Classes classes) {
        if (classes == null) return null;

        return new ClassResponseDTO(
                classes.getId(),
                classes.getName(),
                classes.getCode_class(),
                classes.getAcademicYear(),
                classes.getCourse() != null ? classes.getCourse().getId() : null,
                classes.getCourse() != null ? classes.getCourse().getName() : null
        );
    }
}