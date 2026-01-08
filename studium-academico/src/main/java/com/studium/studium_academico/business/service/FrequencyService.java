package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.FrequencyByRegistrationRequestDTO;
import com.studium.studium_academico.business.dto.request.FrequencyRequestDTO;
import com.studium.studium_academico.business.dto.response.FrequencyResponseDTO;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.exceptions.BusinessValidationException;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.FrequencyMapper;
import com.studium.studium_academico.infrastructure.repository.*;
import com.studium.studium_academico.infrastructure.specification.FrequencySpecifications;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FrequencyService {
    @Autowired
    private FrequencyRepository frequencyRepository;
    @Autowired
    private FrequencyMapper frequencyMapper;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Transactional
    public FrequencyResponseDTO createFrequency(FrequencyRequestDTO dto) {
        if (dto.registrationId() == null) {
            throw new BusinessValidationException("registrationId é obrigatório");
        }

        if (dto.classroomId() == null) {
            throw new BusinessValidationException("classroomId é obrigatório");
        }

        if (dto.attendanceDate() == null) {
            throw new BusinessValidationException("attendanceDate é obrigatória");
        }

        Registration registration = registrationRepository.findById(dto.registrationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Matrícula não encontrada: " + dto.registrationId()));

        Classroom classroom = classroomRepository.findById(dto.classroomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aula não encontrada: " + dto.classroomId()));


        validateDateNotFuture(dto.attendanceDate());
        validateSameClass(registration, classroom);
        validateDisciplineBelongsToCourse(classroom.getDiscipline(), registration.getCourse());
        validateDuplicateFrequency(registration, classroom, dto.attendanceDate());

        Frequency frequency = frequencyMapper.toEntity(dto);
        frequency.setRegistration(registration);
        frequency.setClassroom(classroom);

        Frequency saved = frequencyRepository.save(frequency);
        return frequencyMapper.toResponse(saved);
    }
    @Transactional
    public FrequencyResponseDTO createFrequencyByRegistration(
            FrequencyByRegistrationRequestDTO dto
    ) {
        if (dto.registrationId() == null) {
            throw new BusinessValidationException("registrationId é obrigatório");
        }

        if (dto.disciplineId() == null) {
            throw new BusinessValidationException("disciplineId é obrigatória");
        }

        if (dto.attendanceDate() == null) {
            throw new BusinessValidationException("attendanceDate é obrigatória");
        }

        Registration registration = registrationRepository.findById(dto.registrationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Matrícula não encontrada: " + dto.registrationId())
                );

        // 🔑 Aula SEMESTRAL (turma + disciplina)
        Classroom classroom = classroomRepository
                .findByClassEntityIdAndDisciplineId(
                        registration.getClassEntity().getId(),
                        dto.disciplineId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Disciplina não vinculada à turma da matrícula"
                        )
                );

        validateDateNotFuture(dto.attendanceDate());
        validateSameClass(registration, classroom);
        validateDisciplineBelongsToCourse(classroom.getDiscipline(), registration.getCourse());
        validateDuplicateFrequency(registration, classroom, dto.attendanceDate());

        Frequency frequency = new Frequency();
        frequency.setRegistration(registration);
        frequency.setClassroom(classroom);
        frequency.setAttendanceDate(dto.attendanceDate());
        frequency.setStatusFrequency(dto.statusFrequency());
        frequency.setJustification(dto.justification());

        Frequency saved = frequencyRepository.save(frequency);
        return frequencyMapper.toResponse(saved);
    }


    @Transactional
    public FrequencyResponseDTO updateFrequency(UUID id, FrequencyRequestDTO dto) {
        log.info("updateFrequency - id={}", id);

        Frequency frequency = frequencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Frequência não encontrada: " + id));

        validateUpdateRules(frequency);

        if (dto.registrationId() != null &&
                !dto.registrationId().equals(frequency.getRegistration().getId())) {

            Registration newRegistration = registrationRepository.findById(dto.registrationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada: " + dto.registrationId()));
            frequency.setRegistration(newRegistration);
        }

        // Se trocou de aula: carrega a nova aula e valida coerência com matrícula atual
        if (dto.classroomId() != null &&
                !dto.classroomId().equals(frequency.getClassroom().getId())) {

            Classroom newClassroom = classroomRepository.findById(dto.classroomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Aula não encontrada: " + dto.classroomId()));

            // valida agora com matrícula atual (caso tenha sido trocada acima)
            validateSameClass(frequency.getRegistration(), newClassroom);
            validateDisciplineBelongsToCourse(newClassroom.getDiscipline(), frequency.getRegistration().getCourse());

            frequency.setClassroom(newClassroom);
        }

        frequencyMapper.updateEntityFromDto(dto, frequency);

        // Re-validar duplicidade caso a combinação tenha mudado (registro + aula + data)
        if (dto.attendanceDate() != null) {
            validateDuplicateFrequencyOnUpdate(frequency, dto.attendanceDate());
            validateDateNotFuture(dto.attendanceDate());
        }

        Frequency updated = frequencyRepository.save(frequency);
        return frequencyMapper.toResponse(updated);
    }

    @Transactional
    public void deleteFrequency(UUID id) {
        log.info("deleteFrequency - id={}", id);

        Frequency f = frequencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Frequência não encontrada: " + id));

        if (f.getAttendanceDate().isBefore(LocalDate.now().minusDays(30))) {
            throw new BusinessValidationException("Não é possível excluir frequência com mais de 30 dias");
        }

        if (f.getStatusFrequency() == StatusFrequency.JUSTIFIED) {
            throw new BusinessValidationException("Não é possível excluir frequência já justificada");
        }

        frequencyRepository.delete(f);
    }

    public FrequencyResponseDTO findById(UUID id) {
        Frequency f = frequencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Frequência não encontrada: " + id));

        return frequencyMapper.toResponse(f);
    }


    private void validateDuplicateFrequency(Registration registration, Classroom classroom, LocalDate date) {
        boolean exists = frequencyRepository.existsByRegistrationAndClassroomAndAttendanceDate(registration, classroom, date);
        if (exists) {
            throw new BusinessValidationException("Frequência já registrada para esta matrícula, aula e data");
        }
    }

    private void validateDuplicateFrequencyOnUpdate(Frequency current, LocalDate newDate) {
        Registration reg = current.getRegistration();
        Classroom cls = current.getClassroom();
        if (newDate != null && !newDate.equals(current.getAttendanceDate())) {
            boolean exists = frequencyRepository.existsByRegistrationAndClassroomAndAttendanceDate(reg, cls, newDate);
            if (exists) {
                throw new BusinessValidationException("Já existe frequência para essa matrícula/aula/novo dia");
            }
        }
    }

    private void validateUpdateRules(Frequency frequency) {
        if (frequency.getAttendanceDate().isBefore(LocalDate.now().minusDays(30))) {
            throw new BusinessValidationException("Não é possivel alterar frequência após 30 dias");
        }
    }

    private void validateSameClass(Registration registration, Classroom classroom) {
        if (!registration.getClassEntity().getId().equals(classroom.getClassEntity().getId())) {
            throw new BusinessValidationException("Matrícula não pertence à turma desta aula");
        }
    }

    private void validateDisciplineBelongsToCourse(Discipline discipline, Course course) {
        if (discipline == null || course == null) return;
        boolean belongs = courseRepository.existsByIdAndDisciplinesId(course.getId(), discipline.getId());
        if (!belongs) {
            throw new BusinessValidationException("Disciplina não pertence ao curso da matrícula");
        }
    }

    private void validateDateNotFuture(LocalDate date) {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new BusinessValidationException("Data da frequência não pode ser futura");
        }
    }
}
