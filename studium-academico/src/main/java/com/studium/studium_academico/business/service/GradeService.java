package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.GradeRequestDTO;
import com.studium.studium_academico.business.dto.response.GradeResponseDTO;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.exceptions.BusinessValidationException;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.GradeMapper;
import com.studium.studium_academico.infrastructure.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class GradeService {
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private GradeMapper mapper;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private ClassesRepository classRepository;

    @Transactional
    public GradeResponseDTO createGrade(GradeRequestDTO dto) {
        Registration reg = registrationRepository.findById(dto.registrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

        Teacher teacher = teacherRepository.findById(dto.teacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado"));

        Discipline discipline = disciplineRepository.findById(dto.disciplineId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada"));

        Classes classEntity = classRepository.findById(dto.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada"));

        validateGradeValue(dto.grade());
        validateStudentBelongsToClass(reg, classEntity);
        validateTeacherTeachesDiscipline(teacher, discipline);

        Grade grade = mapper.toEntity(dto);

        grade.setRegistration(reg);
        grade.setTeacher(teacher);
        grade.setDiscipline(discipline);
        grade.setClassEntity(classEntity);

        Grade saved = gradeRepository.save(grade);
        return mapper.toDTO(saved);
    }

    @Transactional
    public GradeResponseDTO updateGrade(UUID id, GradeRequestDTO dto) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada"));

        // atualiza valores simples
        mapper.updateEntityFromDto(dto, grade);

        validateGradeValue(grade.getGrade());

        Grade saved = gradeRepository.save(grade);
        return mapper.toDTO(saved);
    }

    @Transactional
    public void deleteGrade(UUID id) {
        if (!gradeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Nota não encontrada");
        }
        gradeRepository.deleteById(id);
    }

    public GradeResponseDTO findById(UUID id) {
        Grade g = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada"));
        return mapper.toDTO(g);
    }

    private void validateGradeValue(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.TEN) > 0) {
            throw new BusinessValidationException("A nota deve estar entre 0 e 10");
        }
    }

    private void validateStudentBelongsToClass(Registration reg, Classes cls) {
        if (!reg.getClassEntity().getId().equals(cls.getId())) {
            throw new BusinessValidationException("Aluno não pertence a esta turma");
        }
    }

    private void validateTeacherTeachesDiscipline(Teacher teacher, Discipline discipline) {
        if (teacher == null || discipline == null) {
            throw new BusinessValidationException("Professor/Disciplina inválidos");
        }
    }
}
