package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.request.ClassRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Classes;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {CourseMapper.class})
public interface ClassesMapper {

    @Mapping(target = "course", ignore = true) // será configurado no service
    @Mapping(target = "registrations", ignore = true)
    @Mapping(target = "teacherClasses", ignore = true)
    @Mapping(target = "classrooms", ignore = true)
    Classes toEntity(ClassRequestDTO dto);

    @Mapping(source = "codeClass", target = "codeClass")
    ClassResponseDTO toResponse(Classes classes);
}
