package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.RegistrationRequestDTO;
import com.studium.studium_academico.business.dto.response.RegistrationResponseDTO;
import com.studium.studium_academico.business.dto.response.RegistrationValidationResponseDTO;
import com.studium.studium_academico.business.dto.response.StudentInClassResponseDTO;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.repository.ClassesRepository;
import com.studium.studium_academico.infrastructure.repository.CourseRepository;
import com.studium.studium_academico.infrastructure.repository.RegistrationRepository;
import com.studium.studium_academico.infrastructure.repository.StudentRepository;
import com.studium.studium_academico.infrastructure.mapper.RegistrationMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrationService {
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private RegistrationMapper registrationMapper;

    @Transactional
    public RegistrationResponseDTO createRegistration(RegistrationRequestDTO data) {
        Student student = studentRepository.findById(data.studentId())
                .orElseThrow(() -> new RuntimeException("Nenhum aluno encontrado"));

        Course course = courseRepository.findById(data.courseId())
                .orElseThrow(() -> new RuntimeException("Nenhum curso encontrado"));

        Classes classesEntity = classesRepository.findById(data.classId())
                .orElseThrow(() -> new RuntimeException("Nenhuma classe encontrada"));

        if(!classesEntity.getCourse().getId().equals(course.getId())) throw new RuntimeException("A turma não pertence ao curso selecionado");

        if (registrationRepository.existsByStudentIdAndClassEntityId(data.studentId(), data.classId())) throw new RuntimeException("Aluno já está matriculado nesta turma ");

        String registrationNumber = generateRegistrationNumber();

        if(registrationRepository.existsByRegistrationNumber(registrationNumber)) throw new RuntimeException("Número de matricula já existe");

        LocalDate dateRegistration = data.dateRegistration() != null ? data.dateRegistration() : LocalDate.now();

        Registration registration = Registration.builder()
                .registrationNumber(registrationNumber)
                .dateRegistration(dateRegistration)
                .student(student)
                .course(course)
                .classEntity(classesEntity)
                .frequencies(new ArrayList<>())
                .build();

        Registration saved = registrationRepository.save(registration);
        return registrationMapper.toResponseDTO(saved);
    }

    @Transactional
    public RegistrationResponseDTO findById(UUID id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nenhum aluno encontrado"));

        return registrationMapper.toResponseDTO(registration);
    }

    @Transactional
    public RegistrationResponseDTO findByRegistrationNumber(String registrationNumber) {
        Registration registration = registrationRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));

        return registrationMapper.toResponseDTO(registration);
    }

    public List<RegistrationResponseDTO> findAllRegistrations() {
        return registrationRepository.findAll()
                .stream()
                .map(registrationMapper::toResponseDTO)
                .toList();
    }

    public List<RegistrationResponseDTO> findByStudentId(UUID studentId) {
        if(!studentRepository.existsById(studentId)) throw new RuntimeException("Nenhum aluno encontrado");

        return registrationRepository.findByStudentId(studentId)
                .stream()
                .map(registrationMapper::toResponseDTO)
                .toList();
    }

    public List<RegistrationResponseDTO> findByCourseId(UUID courseId) {
        if(!courseRepository.existsById(courseId)) throw new RuntimeException("Nenhum curso encontrado");

        return registrationRepository.findByCourseId(courseId)
                .stream()
                .map(registrationMapper::toResponseDTO)
                .toList();
    }

    public List<RegistrationResponseDTO> findByClassEntityId(UUID classId) {
        if (!classesRepository.existsById(classId)) throw new RuntimeException("Turma não encontrada");

        return registrationRepository.findByClassEntityId(classId)
                .stream()
                .map(registrationMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public RegistrationResponseDTO updateRegistration(UUID id, RegistrationRequestDTO data) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));

        Student student = registration.getStudent();
        if (data.studentId() != null && !data.studentId().equals(student.getId())) {
            student = studentRepository.findById(data.studentId())
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        }

        Course course = registration.getCourse();
        if (data.courseId() != null && !data.courseId().equals(course.getId())) {
            course = courseRepository.findById(data.courseId())
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
        }

        Classes classEntity = registration.getClassEntity();
        if (data.classId() != null && !data.classId().equals(classEntity.getId())) {
            classEntity = classesRepository.findById(data.classId())
                    .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
        }
        if (!classEntity.getCourse().getId().equals(course.getId())) {
            throw new RuntimeException("A turma não pertence ao curso selecionado");
        }

        if (registrationRepository.existsByStudentIdAndClassEntityId(student.getId(), classEntity.getId())) {
            throw new RuntimeException("Aluno já está matriculado nesta turma");
        }

        registration.setStudent(student);
        registration.setCourse(course);
        registration.setClassEntity(classEntity);

        if (data.dateRegistration() != null) {
            registration.setDateRegistration(data.dateRegistration());
        }

        Registration updated = registrationRepository.save(registration);
        return registrationMapper.toResponseDTO(updated);
    }

    public List<StudentInClassResponseDTO> findStudentsByClassId(UUID classId) {

        if (!classesRepository.existsById(classId)) {
            throw new RuntimeException("Turma não encontrada");
        }

        return registrationRepository.findByClassEntityId(classId)
                .stream()
                .map(reg -> new StudentInClassResponseDTO(
                        reg.getId(),
                        reg.getStudent().getId(),
                        reg.getStudent().getUser().getName(),
                        reg.getRegistrationNumber()
                ))
                .toList();
    }

    @Transactional
    public void deleteRegistration(UUID id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));

        // Verificar se existem frequências vinculadas
        if (registration.getFrequencies() != null && !registration.getFrequencies().isEmpty()) {
            throw new RuntimeException("Não é possível excluir a matrícula. Existem frequências vinculadas.");
        }

        registrationRepository.delete(registration);
    }

    // VALIDATE DELETION - Validar se pode excluir (para frontend)
    public RegistrationValidationResponseDTO validateDeletion(UUID id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada"));

        boolean canDelete = true;
        List<String> blockers = new ArrayList<>();

        if (registration.getFrequencies() != null && !registration.getFrequencies().isEmpty()) {
            canDelete = false;
            blockers.add(String.format("%d frequência(s) vinculada(s)", registration.getFrequencies().size()));
        }

        return new RegistrationValidationResponseDTO(canDelete, blockers);
    }

    public Long countByStudentId(UUID studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Aluno não encontrado");
        }
        return registrationRepository.countByStudentId(studentId);
    }

    public Long countByCourseId(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Curso não encontrado");
        }
        return registrationRepository.countByCourseId(courseId);
    }

    public Long countByClassId(UUID classId) {
        if (!classesRepository.existsById(classId)) {
            throw new RuntimeException("Turma não encontrada");
        }
        return registrationRepository.countByClassEntityId(classId);
    }



    public String generateRegistrationNumber() {
        // Lógica para gerar número de matrícula único
        // Formato: REG + ANO + MÊS + SEQUENCIAL (REG2024010001)
        String yearMonth = LocalDate.now().getYear() +
                String.format("%02d", LocalDate.now().getMonthValue());

        Long sequencial = registrationRepository.count() + 1;

        return "REG" + yearMonth + String.format("%04d", sequencial);
    }

}
