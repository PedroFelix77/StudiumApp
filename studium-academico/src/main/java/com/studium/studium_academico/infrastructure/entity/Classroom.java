package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "classroom")
@Getter
@Setter
public class Classroom extends BaseEntity {

    private String code;
    private String name;
    @Column(nullable = false)
    private Integer weeklyWorkLoad;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Classes classEntity;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<Frequency> frequencies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id",  nullable = false)
    private Discipline discipline;
}
