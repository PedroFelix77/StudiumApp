package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.infrastructure.entity.TeacherClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ClassesMapper.class})
public interface TeacherClassMapper {

    @Mapping(source = "classEntity", target = "classEntity")
    @Mapping(target = "subject", ignore = true)
    TeacherClassResponseDTO toResponseDTO(TeacherClass teacherClass);
}