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

        // Média inicial (P1 + P2) / 2
        BigDecimal initialAverage = BigDecimal.ZERO;
        if (p1.compareTo(BigDecimal.ZERO) > 0 && p2.compareTo(BigDecimal.ZERO) > 0) {
            initialAverage = p1.add(p2)
                    .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        } else if (p1.compareTo(BigDecimal.ZERO) > 0) {
            initialAverage = p1; // Se só tem P1
        } else if (p2.compareTo(BigDecimal.ZERO) > 0) {
            initialAverage = p2; // Se só tem P2
        }

        // Se passou direto (média >= 7)
        if (initialAverage.compareTo(BigDecimal.valueOf(7)) >= 0) {
            return new AverageResult(
                    initialAverage,
                    initialAverage, // Não fez final, média final = média inicial
                    StudentStatus.APROVADO,
                    "Aprovado direto"
            );
        }

        // Se não tem final e não passou → reprovado
        if (finalExam.compareTo(BigDecimal.ZERO) == 0) {
            return new AverageResult(
                    initialAverage,
                    initialAverage, // Média final igual à inicial
                    StudentStatus.REPROVADO,
                    "Reprovado por média insuficiente"
            );
        }

        // Fazer final: (média inicial + final) / 2
        BigDecimal finalAverage = initialAverage.add(finalExam)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        StudentStatus status =
                finalAverage.compareTo(BigDecimal.valueOf(5)) >= 0  // Na final, precisa de 5 para passar
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