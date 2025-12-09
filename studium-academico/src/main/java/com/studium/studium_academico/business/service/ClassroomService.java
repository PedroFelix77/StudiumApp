package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.ClassroomRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassroomResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Classes;
import com.studium.studium_academico.infrastructure.entity.Classroom;
import com.studium.studium_academico.infrastructure.entity.Discipline;
import com.studium.studium_academico.infrastructure.mapper.ClassroomMapper;
import com.studium.studium_academico.infrastructure.repository.ClassesRepository;
import com.studium.studium_academico.infrastructure.repository.ClassroomRepository;
import com.studium.studium_academico.infrastructure.repository.DisciplineRepository;
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

    public ClassroomResponseDTO create(ClassroomRequestDTO dto) {

        Classes classEntity = classRepository.findById(dto.classId())
                .orElseThrow(() -> new RuntimeException("Turma não encontrada."));

        Discipline discipline = disciplineRepository.findById(dto.disciplineId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada."));

        Classroom classroom = mapper.toEntity(dto);
        classroom.setClassEntity(classEntity);
        classroom.setDiscipline(discipline);

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
                .orElseThrow(() -> new RuntimeException("Aula não encontrada."));

        classroom.setDate(dto.date());
        classroom.setContent(dto.content());

        Classes classEntity = classRepository.findById(dto.classId())
                .orElseThrow(() -> new RuntimeException("Turma não encontrada."));

        Discipline discipline = disciplineRepository.findById(dto.disciplineId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada."));

        classroom.setClassEntity(classEntity);
        classroom.setDiscipline(discipline);

        return mapper.toResponse(classroomRepository.save(classroom));
    }

    public void delete(UUID id) {
        if (!classroomRepository.existsById(id)) {
            throw new RuntimeException("Aula não encontrada.");
        }
        classroomRepository.deleteById(id);
    }

}
