package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "disciplines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discipline extends BaseEntity {

    @Column(name = "name",  nullable = false)
    private String name;

    @Column(name = "workload")
    private Integer workload;

    @Column(name = "code")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "discipline")
    private List<Grade> grades =  new ArrayList<>();

    @OneToMany(mappedBy = "discipline")
    private List<Classroom> classrooms = new ArrayList<>();
}
