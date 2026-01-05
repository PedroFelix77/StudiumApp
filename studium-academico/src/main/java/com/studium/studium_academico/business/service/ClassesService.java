package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.ClassRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Classes;
import com.studium.studium_academico.infrastructure.entity.Course;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.ClassesMapper;
import com.studium.studium_academico.infrastructure.repository.ClassesRepository;
import com.studium.studium_academico.infrastructure.repository.CourseRepository;
import com.studium.studium_academico.infrastructure.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassesService {

    private final ClassesRepository classesRepository;
    private final CourseRepository courseRepository;
    private final ClassesMapper mapper;
    private final TeacherRepository teacherRepository;

    public ClassResponseDTO create(ClassRequestDTO dto) {

        if (classesRepository.existsByCodeClass(dto.codeClass())) {
            throw new RuntimeException("Já existe uma turma com esse código.");
        }

        Course course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado."));

        Classes entity = mapper.toEntity(dto);
        entity.setCourse(course);

        return mapper.toResponse(classesRepository.save(entity));
    }

    public Page<ClassResponseDTO> findAll(Pageable pageable) {
        return classesRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    public ClassResponseDTO findById(UUID id) {
        return classesRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada."));
    }

    public List<ClassResponseDTO> findByCourseId(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Curso não encontrado");
        }

        return classesRepository.findByCourseId(courseId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ClassResponseDTO update(UUID id, ClassRequestDTO dto) {
        Classes entity = classesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada."));

        entity.setName(dto.name());
        entity.setCodeClass(dto.codeClass());
        entity.setAcademicYear(dto.academicYear());

        Course course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado."));

        entity.setCourse(course);

        return mapper.toResponse(classesRepository.save(entity));
    }

    public void delete(UUID id) {
        if (!classesRepository.existsById(id)) {
            throw new RuntimeException("Turma não encontrada.");
        }
        classesRepository.deleteById(id);
    }

    public List<Classes> findClassesByTeacherAndCourse(
            UUID teacherId,
            UUID courseId
    ) {
        // valida professor
        teacherRepository.findById(teacherId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado"));

        // valida curso
        courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Curso não encontrado"));

        return classesRepository.findByTeacherAndCourse(teacherId, courseId);
    }
}
