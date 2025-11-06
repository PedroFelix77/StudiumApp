package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration extends BaseEntity {

    @Column(name = "enrollment", unique = true, nullable = false)
    private String registrationNumber;
    @Column(name = "date_Registration", nullable = false)
    private LocalDate dateRegistration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Class classEntity;

    @OneToMany(mappedBy = "registration", fetch = FetchType.LAZY)
    private List<Frequency> frequencies = new ArrayList<>();
}
