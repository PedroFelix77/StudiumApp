package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.DepartmentResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentResponseDTO toDTO(Department department);
}
