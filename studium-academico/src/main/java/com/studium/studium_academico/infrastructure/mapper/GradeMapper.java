package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.request.GradeRequestDTO;
import com.studium.studium_academico.business.dto.response.GradeResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Grade;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
        RegistrationMapper.class,
        TeacherMapper.class,
        DisciplineMapper.class,
        ClassesMapper.class
})
public interface GradeMapper{

    @Mapping(target = "registration", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "discipline", ignore = true)
    @Mapping(target = "classEntity", ignore = true)
    Grade toEntity(GradeRequestDTO dto);

    GradeResponseDTO toDTO(Grade grade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "registration", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "discipline", ignore = true)
    @Mapping(target = "classEntity", ignore = true)
    void updateEntityFromDto(GradeRequestDTO dto, @MappingTarget Grade entity);
}