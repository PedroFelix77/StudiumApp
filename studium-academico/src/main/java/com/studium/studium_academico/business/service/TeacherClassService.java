package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.TeacherClassCreateDTO;
import com.studium.studium_academico.business.dto.response.ClassResponseDTO;
import com.studium.studium_academico.business.dto.response.DisciplineResponseDTO;
import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Teacher;
import com.studium.studium_academico.infrastructure.entity.TeacherClass;
import com.studium.studium_academico.infrastructure.exceptions.BusinessValidationException;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.TeacherClassMapper;
import com.studium.studium_academico.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public List<ClassResponseDTO> findClassesByTeacher(UUID teacherId) {
        log.info("Buscando turmas do professor: {}", teacherId);

        return repository.findByTeacherIdWithDetails(teacherId)
                .stream()
                .map(teacherClass -> new ClassResponseDTO(
                        teacherClass.getClassEntity().getId(),
                        teacherClass.getClassEntity().getName(),
                        teacherClass.getClassEntity().getCodeClass(),
                        teacherClass.getClassEntity().getAcademicYear()
                ))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<DisciplineResponseDTO> findDisciplinesByTeacherAndClass(UUID teacherId, UUID classId) {
        log.info("Buscando disciplinas do professor {} na turma {}", teacherId, classId);

        return repository.findByTeacherAndClassWithDisciplineDetails(teacherId, classId)
                .stream()
                .map(teacherClass -> new DisciplineResponseDTO(
                        teacherClass.getDiscipline().getId(),
                        teacherClass.getDiscipline().getName(),
                        teacherClass.getDiscipline().getCode(),
                        teacherClass.getDiscipline().getWorkload(),
                        null, // course
                        null, // teacher
                        null  // classes
                ))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<ClassResponseDTO> findClassesByLoggedTeacher(UUID userId) {

        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado para o usuário logado")
                );

        return repository.findByTeacherIdWithDetails(teacher.getId())
                .stream()
                .map(tc -> new ClassResponseDTO(
                        tc.getClassEntity().getId(),
                        tc.getClassEntity().getName(),
                        tc.getClassEntity().getCodeClass(),
                        tc.getClassEntity().getAcademicYear()
                ))
                .distinct()
                .toList();
    }

    public List<DisciplineResponseDTO> findDisciplinesByLoggedTeacherAndClass(
            UUID userId,
            UUID classId
    ) {

        Teacher teacher = teacherRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Professor não encontrado para o usuário logado")
                );

        return repository.findByTeacherAndClassWithDisciplineDetails(
                        teacher.getId(),
                        classId
                ).stream()
                .map(tc -> new DisciplineResponseDTO(
                        tc.getDiscipline().getId(),
                        tc.getDiscipline().getName(),
                        tc.getDiscipline().getCode(),
                        tc.getDiscipline().getWorkload(),
                        null,
                        null,
                        null
                ))
                .distinct()
                .toList();
    }
}
