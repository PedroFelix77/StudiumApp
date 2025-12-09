package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.request.DisciplineRequestDTO;
import com.studium.studium_academico.business.dto.request.DisciplineUpdateRequestDTO;
import com.studium.studium_academico.business.dto.response.DisciplineResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Discipline;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CourseMapper.class, TeacherMapper.class, ClassesMapper.class})
public interface DisciplineMapper {
    @Mapping(target = "course", source = "course")
    @Mapping(target = "teacher", source = "teacher")
    DisciplineResponseDTO toDto(Discipline discipline);

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "grades", ignore = true)
    @Mapping(target = "classrooms", ignore = true)
    Discipline toEntity(DisciplineRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "grades", ignore = true)
    @Mapping(target = "classrooms", ignore = true)
    void updateEntityFromDto(DisciplineUpdateRequestDTO dto, @MappingTarget Discipline entity);
}