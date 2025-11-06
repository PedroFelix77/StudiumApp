package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password",  nullable = false,  unique = true)
    private String password;

    @Column(name = "birthday",  nullable = false)
    private LocalDate birthday;

    @Column(name = "phone",  nullable = false)
    private String phone;

    @Column(nullable = false)
    private String role;

    @ManyToOne
    @JoinColumn(name = "institution_id",  nullable = false)
    private Institution institution;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Student student;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Teacher teacher;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}
