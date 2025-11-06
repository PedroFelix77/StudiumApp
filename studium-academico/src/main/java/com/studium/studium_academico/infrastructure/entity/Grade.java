package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "grades")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Grade extends BaseEntity {

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type_grade")
    private TypeGrade typeGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registrationStudent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_teacher_id")
    private Teacher recordedByTeacher;

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class classGrade;

}

