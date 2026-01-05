package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.response.AverageResult;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class AverageCalculatorService {

    @Autowired
    private GradeRepository gradeRepository;

    public AverageResult calculateAverage(UUID registrationId, UUID disciplineId) {
        List<Grade> grades = gradeRepository.findByRegistrationIdAndDisciplineId(registrationId, disciplineId);
        return calculate(grades);
    }

    public AverageResult calculateAverage(Registration registration, Discipline discipline) {
        // Se não tiver o método, pode usar este alternativo
        List<Grade> allGrades = gradeRepository.findByRegistrationId(registration.getId());
        List<Grade> filteredGrades = allGrades.stream()
                .filter(g -> g.getDiscipline() != null &&
                        g.getDiscipline().getId().equals(discipline.getId()))
                .toList();
        return calculate(filteredGrades);
    }

    private AverageResult calculate(List<Grade> grades) {
        BigDecimal p1 = getGrade(grades, TypeGrade.PROVA1);
        BigDecimal p2 = getGrade(grades, TypeGrade.PROVA2);
        BigDecimal finalExam = getGrade(grades, TypeGrade.FINAL);

        // Média inicial
        BigDecimal initialAverage = p1.add(p2)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        // Se passou direto
        if (initialAverage.compareTo(BigDecimal.valueOf(7)) >= 0) {
            return new AverageResult(
                    initialAverage,
                    initialAverage,
                    StudentStatus.APROVADO,
                    "Aprovado direto"
            );
        }

        // Se não tem final → reprovado direto
        if (finalExam.compareTo(BigDecimal.ZERO) == 0) {
            return new AverageResult(
                    initialAverage,
                    initialAverage,
                    StudentStatus.REPROVADO,
                    "Reprovado por falta de nota final"
            );
        }

        // Média final (recuperação)
        BigDecimal finalAverage = initialAverage.add(finalExam)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        StudentStatus status =
                finalAverage.compareTo(BigDecimal.valueOf(7)) >= 0
                        ? StudentStatus.APROVADO
                        : StudentStatus.REPROVADO;

        String message = status == StudentStatus.APROVADO
                ? "Aprovado na recuperação"
                : "Reprovado na recuperação";

        return new AverageResult(
                initialAverage,
                finalAverage,
                status,
                message
        );
    }

    private BigDecimal getGrade(List<Grade> grades, TypeGrade type) {
        return grades.stream()
                .filter(g -> g.getTypeGrade() == type)
                .map(Grade::getGrade)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
}