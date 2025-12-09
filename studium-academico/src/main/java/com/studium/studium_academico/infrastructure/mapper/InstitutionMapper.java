package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.InstitutionResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Institution;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, CourseMapper.class, DepartmentMapper.class})
public interface InstitutionMapper {

    InstitutionResponseDTO toResponseDTO(Institution institution);
}