package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.response.AverageResult;
import com.studium.studium_academico.business.dto.response.StudentTranscript;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.repository.DisciplineRepository;
import com.studium.studium_academico.infrastructure.repository.GradeRepository;
import com.studium.studium_academico.infrastructure.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TranscriptService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private AverageCalculatorService averageCalculatorService;

    public StudentTranscript generateTranscript(UUID registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula não encontrada"));

        List<UUID> disciplineIds = gradeRepository.findByRegistrationId(registrationId).stream()
                .map(g -> g.getDiscipline().getId())
                .distinct()
                .toList();

        List<StudentTranscript.DisciplineResult> results = new ArrayList<>();

        for (UUID disciplineId : disciplineIds) {
            Discipline discipline = disciplineRepository.findById(disciplineId).orElse(null);
            if (discipline != null) {
                AverageResult average = averageCalculatorService.calculateAverage(registrationId, disciplineId);
                results.add(new StudentTranscript.DisciplineResult(
                        discipline.getName(),
                        discipline.getCode(),
                        average.initialAverage(),
                        average.finalAverage(),
                        average.status()
                ));
            }
        }

        return new StudentTranscript(
                registration.getStudent().getUser().getName(),
                registration.getRegistrationNumber(),
                registration.getClassEntity().getName(),
                results
        );
    }
}