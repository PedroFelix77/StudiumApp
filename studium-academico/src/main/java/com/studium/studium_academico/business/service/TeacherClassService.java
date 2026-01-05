package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.TeacherClassCreateDTO;
import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.infrastructure.entity.TeacherClass;
import com.studium.studium_academico.infrastructure.exceptions.BusinessValidationException;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.TeacherClassMapper;
import com.studium.studium_academico.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherClassService {

    private final TeacherClassRepository repository;
    private final TeacherRepository teacherRepository;
    private final ClassesRepository classRepository;
    private final CourseRepository courseRepository;
    private final DisciplineRepository disciplineRepository;
    private final TeacherClassMapper mapper;

    public TeacherClassResponseDTO create(TeacherClassCreateDTO dto) {

        if (repository.existsByTeacherIdAndClassEntityIdAndDisciplineId(
                dto.teacherId(), dto.classId(), dto.disciplineId())) {
            throw new BusinessValidationException("Professor já alocado nessa turma e disciplina");
        }

        TeacherClass tc = TeacherClass.builder()
                .teacher(teacherRepository.findById(dto.teacherId()).orElseThrow())
                .classEntity(classRepository.findById(dto.classId()).orElseThrow())
                .course(courseRepository.findById(dto.courseId()).orElseThrow())
                .discipline(disciplineRepository.findById(dto.disciplineId()).orElseThrow())
                .weeklyHours(dto.weeklyHours())
                .isMainTeacher(dto.isMainTeacher())
                .startDate(LocalDate.now())
                .build();

        return mapper.toResponseDTO(repository.save(tc));
    }

    public List<TeacherClassResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toResponseDTO).toList();
    }

    public List<TeacherClassResponseDTO> findByTeacher(UUID teacherId) {
        return repository.findByTeacherId(teacherId).stream().map(mapper::toResponseDTO).toList();
    }

    public List<TeacherClassResponseDTO> findByTeacherAndCourse(UUID teacherId, UUID courseId) {
        return repository.findByTeacherIdAndCourseId(teacherId, courseId)
                .stream().map(mapper::toResponseDTO).toList();
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public List<TeacherClassResponseDTO> findByClass(UUID classId) {
        return repository.findByClassEntityId(classId).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public TeacherClassResponseDTO update(UUID id, TeacherClassCreateDTO dto) {
        TeacherClass teacherClass = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alocação de professor não encontrada"));

        // Atualiza os dados
        teacherClass.setTeacher(teacherRepository.findById(dto.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado")));
        teacherClass.setClassEntity(classRepository.findById(dto.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada")));
        teacherClass.setCourse(courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado")));
        teacherClass.setDiscipline(disciplineRepository.findById(dto.disciplineId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada")));
        teacherClass.setWeeklyHours(dto.weeklyHours());
        teacherClass.setIsMainTeacher(dto.isMainTeacher());

        return mapper.toResponseDTO(repository.save(teacherClass));
    }
}
