package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.request.CourseRequestDTO;
import com.studium.studium_academico.business.dto.request.CourseUpdateRequestDTO;
import com.studium.studium_academico.business.dto.response.CourseResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Course;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    // ENTITY -> DTO
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "institutionId", source = "institution.id")
    @Mapping(target = "institutionName", source = "institution.name")
    CourseResponseDTO toDto(Course entity);

    // DTO -> ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disciplines", ignore = true)
    @Mapping(target = "classes", ignore = true)
    @Mapping(target = "registrations", ignore = true)
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "institution", ignore = true)
    Course toEntity(CourseRequestDTO dto);

    // UPDATE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "disciplines", ignore = true)
    @Mapping(target = "classes", ignore = true)
    @Mapping(target = "registrations", ignore = true)
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "institution", ignore = true)
    void updateEntityFromDto(CourseUpdateRequestDTO dto, @MappingTarget Course entity);
}

