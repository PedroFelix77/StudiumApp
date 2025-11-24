package com.studium.studium_academico.mapper;

import com.studium.studium_academico.business.dto.response.CourseResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Course;
import org.springframework.stereotype.Component;

@Component("courseMapper")
public class CourseMapper {

    public CourseResponseDTO toResponseDTO(Course course) {
        if (course == null) return null;

        return new CourseResponseDTO(
                course.getId(),
                course.getName(),
                course.getCode_course(),
                null, // department - ignore
                null, // institution - ignore
                null, // disciplines - ignore
                null, // classes - ignore
                null, // registrations - ignore
                null  // teachers - ignore
        );
    }
}