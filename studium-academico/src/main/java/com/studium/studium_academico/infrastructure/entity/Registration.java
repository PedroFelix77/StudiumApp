package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registration",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_registration_student_class",
                        columnNames = {"student_id", "class_id"}
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration extends BaseEntity {

    @Column(name = "enrollment", unique = true, nullable = false)
    private String registrationNumber;
    @Column(name = "date_registration", nullable = false)
    private LocalDate dateRegistration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Classes classEntity;

    @OneToMany(mappedBy = "registration", fetch = FetchType.LAZY)
    private List<Frequency> frequencies = new ArrayList<>();
}
