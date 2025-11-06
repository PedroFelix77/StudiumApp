package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "classes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Class extends BaseEntity {

    @Column(name = "name",  nullable = false)
    private String name;
    @Column(name = "code_class",  nullable = false)
    private String code_class;
    @Column(name = "academicYear",   nullable = false)
    private String academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TeacherClass> teacherClasses = new ArrayList<>();

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private List<Classroom> classrooms = new ArrayList<>();

}