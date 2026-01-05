package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.DisciplineRequestDTO;
import com.studium.studium_academico.business.dto.request.DisciplineUpdateRequestDTO;
import com.studium.studium_academico.business.dto.response.DisciplineResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Discipline;
import com.studium.studium_academico.infrastructure.mapper.DisciplineMapper;
import com.studium.studium_academico.infrastructure.repository.CourseRepository;
import com.studium.studium_academico.infrastructure.repository.DisciplineRepository;
import com.studium.studium_academico.infrastructure.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisciplineService {

    private final DisciplineRepository disciplineRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final DisciplineMapper mapper;

    public DisciplineResponseDTO createDiscipline(DisciplineRequestDTO data) {
        Discipline discipline = mapper.toEntity(data);

        if(data.teacherId() != null) {
            var teacher = teacherRepository.findById(data.teacherId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            discipline.setTeacher(teacher);
        }

        var course = courseRepository.findById(data.courseId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        discipline.setCourse(course);

        disciplineRepository.save(discipline);

        return  mapper.toDto(discipline);
    }

    public DisciplineResponseDTO updateDiscipline(UUID id, DisciplineUpdateRequestDTO data) {
        Discipline discipline = disciplineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina inexistente"));

        mapper.updateEntityFromDto(data, discipline);

        if(data.teacherId() != null) {
            var teacher =  teacherRepository.findById(data.teacherId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
            discipline.setTeacher(teacher);
        }
        disciplineRepository.save(discipline);
        return  mapper.toDto(discipline);
    }

    public DisciplineResponseDTO findById(UUID id) {
        return disciplineRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada"));
    }

    public DisciplineResponseDTO findByCode(String code) {
        return disciplineRepository.findByCode(code)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Disciplina inexistente"));
    }


    public void delete(UUID id) {
        disciplineRepository.deleteById(id);
    }

    public List<DisciplineResponseDTO> findByCourseId(UUID courseId) {
        List<Discipline> disciplines = disciplineRepository.findByCourseId(courseId);
        return disciplines
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<DisciplineResponseDTO> findAll() {
        List<Discipline> disciplines = disciplineRepository.findAll();
        return disciplines.stream()
                .map(mapper::toDto)
                .toList();
    }

    public Page<DisciplineResponseDTO> findAll(Pageable pageable) {
        return disciplineRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    public List<DisciplineResponseDTO> findByTeacherId(UUID teacherId) {
        List<Discipline> disciplines = disciplineRepository.findByTeacherId(teacherId);
        return disciplines.stream()
                .map(mapper::toDto)
                .toList();
    }
}
