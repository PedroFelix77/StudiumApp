package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Student extends BaseEntity{

    @OneToMany(mappedBy = "student",cascade = {CascadeType.PERSIST,  CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Registration> registrations =  new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

}
