package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.ClassroomRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassroomResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Classes;
import com.studium.studium_academico.infrastructure.entity.Classroom;
import com.studium.studium_academico.infrastructure.entity.Discipline;
import com.studium.studium_academico.infrastructure.entity.Teacher;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.ClassroomMapper;
import com.studium.studium_academico.infrastructure.repository.ClassesRepository;
import com.studium.studium_academico.infrastructure.repository.ClassroomRepository;
import com.studium.studium_academico.infrastructure.repository.DisciplineRepository;
import com.studium.studium_academico.infrastructure.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClassroomService {
    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private ClassesRepository classRepository;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private ClassroomMapper mapper;
    @Autowired
    private TeacherRepository teacherRepository;

    @Transactional
    public ClassroomResponseDTO create(ClassroomRequestDTO dto) {

        Classes classEntity = classRepository.findById(dto.classId())
                .orElseThrow(() -> new RuntimeException("Turma não encontrada."));

        Discipline discipline = disciplineRepository.findById(dto.disciplineId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada."));

        Teacher teacher = teacherRepository.findById(dto.teacherId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado."));

        Classroom classroom = new Classroom();
        classroom.setName(dto.name());
        classroom.setCode(dto.code());
        classroom.setWeeklyWorkLoad(dto.weeklyWorkLoad());
        classroom.setClassEntity(classEntity);
        classroom.setDiscipline(discipline);
        classroom.setTeacher(teacher);

        return mapper.toResponse(classroomRepository.save(classroom));
    }

    public Page<ClassroomResponseDTO> findAll(Pageable pageable) {
        return classroomRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    public ClassroomResponseDTO findById(UUID id) {
        return classroomRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Aula não encontrada."));
    }

    public ClassroomResponseDTO update(UUID id, ClassroomRequestDTO dto) {

        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta de disciplina não encontrada."));

        Classes classEntity = classRepository.findById(dto.classId())
                .orElseThrow(() -> new RuntimeException("Turma não encontrada."));

        Discipline discipline = disciplineRepository.findById(dto.disciplineId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada."));

        Teacher teacher = teacherRepository.findById(dto.teacherId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado."));

        classroom.setName(dto.name());
        classroom.setCode(dto.code());
        classroom.setWeeklyWorkLoad(dto.weeklyWorkLoad());
        classroom.setClassEntity(classEntity);
        classroom.setDiscipline(discipline);
        classroom.setTeacher(teacher);

        return mapper.toResponse(classroomRepository.save(classroom));
    }


    public void delete(UUID id) {
        if (!classroomRepository.existsById(id)) {
            throw new RuntimeException("Aula não encontrada.");
        }
        classroomRepository.deleteById(id);
    }

    public Classroom findByClassAndDiscipline(UUID classId, UUID disciplineId) {
        return classroomRepository
                .findByClassEntityIdAndDisciplineId(classId, disciplineId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aula não encontrada para a turma e disciplina informadas"
                ));
    }

}
