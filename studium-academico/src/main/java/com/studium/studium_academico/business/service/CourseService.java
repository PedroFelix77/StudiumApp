package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.CourseRequestDTO;
import com.studium.studium_academico.business.dto.request.CourseUpdateRequestDTO;
import com.studium.studium_academico.business.dto.response.CourseResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Course;
import com.studium.studium_academico.infrastructure.entity.Department;
import com.studium.studium_academico.infrastructure.entity.Institution;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.CourseMapper;
import com.studium.studium_academico.infrastructure.repository.CourseRepository;
import com.studium.studium_academico.infrastructure.repository.DepartmentRepository;
import com.studium.studium_academico.infrastructure.repository.InstitutionRepository;
import com.studium.studium_academico.infrastructure.repository.TeacherClassRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final InstitutionRepository institutionRepository;
    private final CourseMapper courseMapper;
    private final TeacherClassRepository teacherClassRepository;

    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO data) {

        if(courseRepository.existsByCodeCourse(data.code_course())){
            throw new IllegalArgumentException("Já existe um curso com esse código");
        }

        Course course = courseMapper.toEntity(data);

        if(data.departmentId() != null){
            Department dept = departmentRepository.findById(data.departmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));
            course.setDepartment(dept);
        }

        if(data.institutionId() != null){
            Institution inst = institutionRepository.findFirstByOrderByIdAsc()
                    .orElseThrow(() -> new ResourceNotFoundException("Instituição não encontrada"));
            course.setInstitution(inst);
        }

        Course saved =  courseRepository.save(course);

        return courseMapper.toDto(saved);
    }

    @Transactional
    public CourseResponseDTO findById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        return courseMapper.toDto(course);
    }

    @Transactional
    public Page<CourseResponseDTO> list(Pageable pageable) {
        return courseRepository.findAll(pageable).map(courseMapper::toDto);
    }

    public CourseResponseDTO update(UUID id, CourseUpdateRequestDTO data) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));

        if(!course.getCodeCourse().equals(data.codeCourse()) && courseRepository.existsByCodeCourse(data.codeCourse())){
            throw new IllegalArgumentException("Já existe um curso com esse código.");
        }

        course.setName(data.name());
        course.setCodeCourse(data.codeCourse());

        if(data.departmentId() != null){
            Department dept = departmentRepository.findById(data.departmentId())
                    .orElseThrow(()-> new ResourceNotFoundException("Departamento não encontrado"));
            course.setDepartment(dept);
        } else {
            course.setDepartment(null);
        }

        if(data.institutionId() != null){
            Institution inst = institutionRepository.findFirstByOrderByIdAsc()
                    .orElseThrow(() -> new ResourceNotFoundException("Instituição não encontrada"));
            course.setInstitution(inst);
        } else {
            course.setInstitution(null);
        }

        Course updated = courseRepository.save(course);

        return courseMapper.toDto(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Course c = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado"));
        courseRepository.delete(c);
    }

    public List<CourseResponseDTO> findByTeacher(UUID teacherId) {
        return teacherClassRepository.findCoursesByTeacher(teacherId)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }
}
