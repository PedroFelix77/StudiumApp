package com.studium.studium_academico.mapper;

import com.studium.studium_academico.business.dto.response.DisciplineResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Discipline;
import org.springframework.stereotype.Component;

@Component("disciplineMapper")
public class DisciplineMapper {

    public DisciplineResponseDTO toResponseDTO(Discipline discipline) {
        if (discipline == null) return null;

        return new DisciplineResponseDTO(
                discipline.getId(),
                discipline.getName(),
                discipline.getCode(),
                discipline.getWorkload(),
                null, // course - ignore
                null, // teacher - ignore
                null  // classes - ignore
        );
    }
}