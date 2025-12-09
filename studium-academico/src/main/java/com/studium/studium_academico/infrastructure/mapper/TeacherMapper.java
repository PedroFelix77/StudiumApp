package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.TeacherResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TeacherMapper {

    @Mapping(target = "courses", ignore = true)     // Ignora relações complexas
    @Mapping(target = "disciplines", ignore = true) // Ignora relações complexas
    TeacherResponseDTO toResponseDTO(Teacher teacher);
}