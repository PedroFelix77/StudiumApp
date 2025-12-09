package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.request.FrequencyRequestDTO;
import com.studium.studium_academico.business.dto.response.FrequencyResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Frequency;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {FrequencyMapper.class})
public interface FrequencyMapper {
    @Mapping(target = "registration", ignore = true)
    @Mapping(target = "classroom", ignore = true)
    @Mapping(target = "registeredByTeacher", ignore = true)
    Frequency toEntity(FrequencyRequestDTO dto);

    @Mapping(target = "registration", source = "registration")
    @Mapping(target = "classroom", source = "classroom")
    @Mapping(target = "registeredByTeacher", source = "registeredByTeacher")
    FrequencyResponseDTO toResponse(Frequency entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "registration", ignore = true),
            @Mapping(target = "classroom", ignore = true),
            @Mapping(target = "registeredByTeacher", ignore = true),
            @Mapping(target = "attendanceDate", source = "attendanceDate"),
            @Mapping(target = "statusFrequency", source = "statusFrequency"),
            @Mapping(target = "justification", source = "justification")
    })
    void updateEntityFromDto(FrequencyRequestDTO dto, @MappingTarget Frequency entity);
}
