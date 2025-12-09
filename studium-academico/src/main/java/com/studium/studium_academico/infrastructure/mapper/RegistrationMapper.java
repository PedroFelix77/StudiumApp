package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.RegistrationResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Registration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.user.name", target = "studentName")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.name", target = "courseName")
    @Mapping(source = "classEntity.id", target = "classId")
    @Mapping(source = "classEntity.name", target = "className")
    RegistrationResponseDTO toResponseDTO(Registration registration);
}