package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.infrastructure.entity.TeacherClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ClassesMapper.class})
public interface TeacherClassMapper {

    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "classEntity.id", target = "classId")
    @Mapping(source = "classEntity.name", target = "className")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.name", target = "courseName")
    @Mapping(source = "discipline.id", target = "disciplineId")
    @Mapping(source = "discipline.name", target = "disciplineName")
    TeacherClassResponseDTO toResponseDTO(TeacherClass teacherClass);
}