package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.GradeRequestDTO;
import com.studium.studium_academico.business.dto.response.AverageResult;
import com.studium.studium_academico.business.dto.response.GradeResponseDTO;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.exceptions.BusinessValidationException;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.GradeMapper;
import com.studium.studium_academico.infrastructure.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
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

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private AverageCalculatorService averageCalculatorService;

    // ==================== CRUD OPERATIONS ====================

    @Transactional
    public GradeResponseDTO createGrade(GradeRequestDTO dto) {
        log.info("Criando nota: {}", dto);

        // Validações básicas
        validateRequiredFields(dto);

        // Busca entidades relacionadas
        Registration reg = registrationRepository.findById(dto.registrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada: " + dto.registrationId()));

        Discipline discipline = disciplineRepository.findById(dto.disciplineId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada: " + dto.disciplineId()));

        Classes classEntity = classRepository.findById(dto.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + dto.classId()));

        // Busca professor da aula (classroom)
        Teacher teacher = findTeacherForClassAndDiscipline(classEntity, discipline);

        // Validações de negócio
        validateGradeValue(dto.grade());
        validateStudentBelongsToClass(reg, classEntity);
        validateTeacherTeachesDiscipline(teacher, discipline);
        validateDuplicateGrade(reg, discipline, dto.typeGrade());

        // Cria e salva a nota
        Grade grade = mapper.toEntity(dto);
        grade.setRegistration(reg);
        grade.setTeacher(teacher);
        grade.setDiscipline(discipline);
        grade.setClassEntity(classEntity);

        Grade saved = gradeRepository.save(grade);
        log.info("Nota criada com ID: {}", saved.getId());

        return mapper.toDTO(saved);
    }



    @Transactional
    public GradeResponseDTO updateGrade(UUID id, GradeRequestDTO dto) {
        log.info("Atualizando nota ID: {}", id);

        if (id == null) {
            throw new BusinessValidationException("ID da nota não pode ser nulo");
        }

        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada: " + id));

        // Atualiza campos básicos
        mapper.updateEntityFromDto(dto, grade);

        // Valida nota
        validateGradeValue(grade.getGrade());

        // Se mudou a disciplina ou turma, valida novamente
        if (dto.disciplineId() != null && !dto.disciplineId().equals(grade.getDiscipline().getId())) {
            Discipline newDiscipline = disciplineRepository.findById(dto.disciplineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada: " + dto.disciplineId()));
            grade.setDiscipline(newDiscipline);
        }

        if (dto.classId() != null && !dto.classId().equals(grade.getClassEntity().getId())) {
            Classes newClass = classRepository.findById(dto.classId())
                    .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + dto.classId()));
            grade.setClassEntity(newClass);
        }

        Grade saved = gradeRepository.save(grade);
        log.info("Nota atualizada: {}", saved.getId());

        return mapper.toDTO(saved);
    }

    @Transactional
    public void deleteGrade(UUID id) {
        log.info("Deletando nota ID: {}", id);

        if (id == null) {
            throw new BusinessValidationException("ID da nota não pode ser nulo");
        }

        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada: " + id));

        // Validações para exclusão (opcional)
        if (grade.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30))) {
            throw new BusinessValidationException("Não é possível excluir notas com mais de 30 dias");
        }

        gradeRepository.delete(grade);
        log.info("Nota deletada: {}", id);
    }

    public GradeResponseDTO findById(UUID id) {
        if (id == null) {
            throw new BusinessValidationException("ID da nota não pode ser nulo");
        }

        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota não encontrada: " + id));

        return mapper.toDTO(grade);
    }

    // ==================== QUERY OPERATIONS ====================

    public Page<GradeResponseDTO> findAll(Pageable pageable) {
        log.info("Listando todas as notas, página: {}", pageable.getPageNumber());
        return gradeRepository.findAll(pageable)
                .map(mapper::toDTO);
    }

    public List<GradeResponseDTO> findAllByClassAndDiscipline(UUID classId, UUID disciplineId) {
        log.info("Buscando notas por turma: {} e disciplina: {}", classId, disciplineId);

        List<Grade> grades = gradeRepository.findByClassEntityIdAndDisciplineId(classId, disciplineId);
        return grades.stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<GradeResponseDTO> findByRegistrationAndDiscipline(UUID registrationId, UUID disciplineId) {
        log.info("Buscando notas por matrícula: {} e disciplina: {}", registrationId, disciplineId);

        List<Grade> grades = gradeRepository.findByRegistrationIdAndDisciplineId(registrationId, disciplineId);
        return grades.stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<GradeResponseDTO> findByStudentAndDiscipline(UUID studentId, UUID disciplineId) {
        log.info("Buscando notas por aluno: {} e disciplina: {}", studentId, disciplineId);

        // Primeiro busca a matrícula do aluno na turma
        List<Registration> registrations = registrationRepository.findByStudentId(studentId);

        if (registrations.isEmpty()) {
            throw new ResourceNotFoundException("Aluno não encontrado ou sem matrícula: " + studentId);
        }

        // Busca notas para cada matrícula (um aluno pode ter mais de uma matrícula)
        return registrations.stream()
                .flatMap(registration ->
                        gradeRepository.findByRegistrationIdAndDisciplineId(
                                registration.getId(),
                                disciplineId
                        ).stream()
                )
                .map(mapper::toDTO)
                .toList();
    }

    public Page<GradeResponseDTO> filterGrades(UUID courseId, UUID classId, UUID disciplineId, Pageable pageable) {
        log.info("Filtrando notas - curso: {}, turma: {}, disciplina: {}", courseId, classId, disciplineId);

        Page<Grade> grades;

        if (courseId != null && classId != null && disciplineId != null) {
            // Filtro completo
            grades = gradeRepository.findByCourseIdAndClassIdAndDisciplineId(courseId, classId, disciplineId, pageable);
        } else if (classId != null && disciplineId != null) {
            // Turma e disciplina
            grades = gradeRepository.findByClassEntityIdAndDisciplineId(classId, disciplineId, pageable);
        } else if (courseId != null) {
            // Apenas curso
            grades = gradeRepository.findByCourseId(courseId, pageable);
        } else {
            // Sem filtros, retorna todas
            grades = gradeRepository.findAll(pageable);
        }

        return grades.map(mapper::toDTO);
    }

    // ==================== BUSINESS OPERATIONS ====================

    public AverageResult getStudentAverage(UUID registrationId, UUID disciplineId) {
        log.info("Calculando média para matrícula: {} e disciplina: {}", registrationId, disciplineId);

        validateIdsNotNull(registrationId, disciplineId);

        // Valida se a matrícula existe
        registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada: " + registrationId));

        // Valida se a disciplina existe
        disciplineRepository.findById(disciplineId)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina não encontrada: " + disciplineId));

        return averageCalculatorService.calculateAverage(registrationId, disciplineId);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void validateRequiredFields(GradeRequestDTO dto) {
        if (dto.registrationId() == null) {
            throw new BusinessValidationException("registrationId é obrigatório");
        }
        if (dto.disciplineId() == null) {
            throw new BusinessValidationException("disciplineId é obrigatório");
        }
        if (dto.classId() == null) {
            throw new BusinessValidationException("classId é obrigatório");
        }
        if (dto.typeGrade() == null) {
            throw new BusinessValidationException("typeGrade é obrigatório");
        }
        if (dto.grade() == null) {
            throw new BusinessValidationException("grade é obrigatória");
        }
    }

    private void validateIdsNotNull(UUID registrationId, UUID disciplineId) {
        if (registrationId == null) {
            throw new BusinessValidationException("registrationId não pode ser nulo");
        }
        if (disciplineId == null) {
            throw new BusinessValidationException("disciplineId não pode ser nulo");
        }
    }

    private Teacher findTeacherForClassAndDiscipline(Classes classEntity, Discipline discipline) {
        return classroomRepository
                .findByClassEntityIdAndDisciplineId(classEntity.getId(), discipline.getId())
                .map(Classroom::getTeacher)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Não há professor alocado para a disciplina " + discipline.getName() +
                                " na turma " + classEntity.getName()
                ));
    }

    private void validateGradeValue(BigDecimal value) {
        if (value == null) {
            throw new BusinessValidationException("A nota não pode ser nula");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.TEN) > 0) {
            throw new BusinessValidationException("A nota deve estar entre 0 e 10");
        }
    }

    private void validateStudentBelongsToClass(Registration reg, Classes cls) {
        if (!reg.getClassEntity().getId().equals(cls.getId())) {
            throw new BusinessValidationException(
                    "Aluno " + reg.getStudent().getUser().getName() +
                            " não pertence à turma " + cls.getName()
            );
        }
    }

    private void validateTeacherTeachesDiscipline(Teacher teacher, Discipline discipline) {
        if (teacher == null) {
            throw new BusinessValidationException("Professor não encontrado para esta disciplina");
        }
        if (discipline == null) {
            throw new BusinessValidationException("Disciplina inválida");
        }
    }

    private void validateDuplicateGrade(Registration registration, Discipline discipline, TypeGrade typeGrade) {
        boolean exists = gradeRepository.existsByRegistrationAndDisciplineAndTypeGrade(
                registration, discipline, typeGrade
        );
        if (exists) {
            throw new BusinessValidationException(
                    String.format("Já existe uma nota do tipo %s para este aluno nesta disciplina", typeGrade)
            );
        }
    }
}