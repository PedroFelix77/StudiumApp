package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.DirectorResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Director;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DirectorMapper {
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.cpf", target = "cpf")
    @Mapping(source = "user.email", target = "email")
    DirectorResponseDTO toResponseDTO(Director director);
}
