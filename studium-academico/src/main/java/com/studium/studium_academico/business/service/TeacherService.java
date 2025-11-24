package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.business.dto.response.TeacherResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Teacher;
import com.studium.studium_academico.infrastructure.entity.TeacherClass;
import com.studium.studium_academico.infrastructure.repository.*;
import com.studium.studium_academico.mapper.TeacherClassMapper;
import com.studium.studium_academico.mapper.TeacherMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private TeacherClassRepository teacherClassRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private FrequencyRepository frequencyRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private TeacherClassMapper  teacherClassMapper;

    public TeacherResponseDTO getMyProfile(UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        return teacherMapper.toResponseDTO(teacher);
    }

    public List<TeacherClassResponseDTO> getMyClasses(UUID teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        List<TeacherClass> teacherClasses = teacherClassRepository.findByTeacherId(teacherId);

        return teacherClasses // preciso criar esse método
                .stream()
                .map(teacherClassMapper::toResponseDTO)
                .toList();
    }

    public List<TeacherResponseDTO> getTeachersByCourse(UUID courseId) {
        return teacherRepository.findByCoursesId(courseId)
                .stream()
                .map(teacherMapper::toResponseDTO)
                .toList();
    }
}
