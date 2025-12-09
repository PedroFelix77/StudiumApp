package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.response.FrequencyResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Frequency;
import com.studium.studium_academico.infrastructure.entity.StatusFrequency;
import com.studium.studium_academico.infrastructure.mapper.FrequencyMapper;
import com.studium.studium_academico.infrastructure.repository.ClassesRepository;
import com.studium.studium_academico.infrastructure.repository.FrequencyRepository;
import com.studium.studium_academico.infrastructure.specification.FrequencySpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class FrequencyFilterService {
    @Autowired
    private FrequencyMapper mapper;

    @Autowired
    private FrequencyRepository frequencyRepository;

    public Page<FrequencyResponseDTO> filterGlobal(
            UUID studentId,
            UUID registrationId,
            UUID disciplineId,
            UUID courseId,
            UUID teacherId,
            UUID classId,
            UUID classroomId,
            StatusFrequency status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Frequency> spec = buildGlobalSpecification(
                studentId, registrationId, disciplineId, courseId,
                teacherId, classId, classroomId, status, startDate, endDate
        );

        return frequencyRepository.findAll(spec, pageable)
                .map(mapper::toResponse);
    }

    public Page<FrequencyResponseDTO> filterForStudent(
            UUID studentId,
            UUID disciplineId,
            StatusFrequency status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Frequency> spec = Specification.allOf(
                FrequencySpecifications.hasStudent(studentId),
                FrequencySpecifications.hasDiscipline(disciplineId),
                FrequencySpecifications.hasStatus(status),
                FrequencySpecifications.betweenDates(startDate, endDate)
        );

        return frequencyRepository.findAll(spec, pageable)
                .map(mapper::toResponse);
    }

    public Page<FrequencyResponseDTO> filterForTeacher(
            UUID teacherId,
            UUID disciplineId,
            UUID classId,
            UUID classroomId,
            StatusFrequency status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Frequency> spec = Specification.allOf(
                FrequencySpecifications.hasTeacher(teacherId),
                FrequencySpecifications.hasDiscipline(disciplineId),
                FrequencySpecifications.hasClassId(classId),
                FrequencySpecifications.hasClassroom(classroomId),
                FrequencySpecifications.hasStatus(status),
                FrequencySpecifications.betweenDates(startDate, endDate)
        );

        return frequencyRepository.findAll(spec, pageable)
                .map(mapper::toResponse);
    }

    public Page<FrequencyResponseDTO> filterForCourse(
            UUID courseId,
            UUID disciplineId,
            UUID classId,
            StatusFrequency status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        Specification<Frequency> spec = Specification.allOf(
                FrequencySpecifications.hasCourse(courseId),
                FrequencySpecifications.hasDiscipline(disciplineId),
                FrequencySpecifications.hasClassId(classId),
                FrequencySpecifications.hasStatus(status),
                FrequencySpecifications.betweenDates(startDate, endDate)
        );

        return frequencyRepository.findAll(spec, pageable)
                .map(mapper::toResponse);
    }

    private Specification<Frequency> buildGlobalSpecification(
            UUID studentId, UUID registrationId, UUID disciplineId,
            UUID courseId, UUID teacherId, UUID classId, UUID classroomId,
            StatusFrequency status, LocalDate startDate, LocalDate endDate
    ) {
        return Specification.allOf(
                FrequencySpecifications.hasStudent(studentId),
                FrequencySpecifications.hasRegistration(registrationId),
                FrequencySpecifications.hasDiscipline(disciplineId),
                FrequencySpecifications.hasCourse(courseId),
                FrequencySpecifications.hasTeacher(teacherId),
                FrequencySpecifications.hasClassId(classId),
                FrequencySpecifications.hasClassroom(classroomId),
                FrequencySpecifications.hasStatus(status),
                FrequencySpecifications.betweenDates(startDate, endDate)
        );
    }
}
