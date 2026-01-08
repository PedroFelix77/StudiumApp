package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.request.ClassroomRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassroomResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Classroom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ClassesMapper.class, DisciplineMapper.class})
public interface ClassroomMapper {

    ClassroomResponseDTO toResponse(Classroom classroom);
}
