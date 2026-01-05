package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"registration_id", "discipline_id"}
        )
)
@Getter
@Setter
@RequiredArgsConstructor
public class StudentDisciplineResult extends BaseEntity {

    @ManyToOne(optional = false)
    private Registration registration;

    @ManyToOne(optional = false)
    private Discipline discipline;

    @ManyToOne(optional = false)
    private Classes classEntity;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal average;

    @Enumerated(EnumType.STRING)
    private StudentStatus studentStatus; // APROVADO, REPROVADO, RECUPERACAO

    private BigDecimal finalGrade; // se houver recuperação/final

    private Integer totalClasses;
    private Integer attendedClasses;
    private BigDecimal attendancePercentage;
}
