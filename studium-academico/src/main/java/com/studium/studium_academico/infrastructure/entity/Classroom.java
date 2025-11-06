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

    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private String content; //conteudo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Class classEntity;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<Frequency> frequencies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;
}
