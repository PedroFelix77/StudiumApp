package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.StudentResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface StudentMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.email", target = "email")
    @Mapping(target = "courses", ignore = true) // Ignora por enquanto
    @Mapping(target = "grades", ignore = true)  // Ignora por enquanto
    StudentResponseDTO toResponseDTO(Student student);
}