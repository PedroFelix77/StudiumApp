package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "institution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institution extends BaseEntity {

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "cnpj",unique = true, nullable = false, length = 14)
    private String cnpj;

    @Column(name = "telephone", nullable = false)
    private String telephone;

    @Column(name = "email",unique = true, nullable = false)
    private String email;

    @Column(name = "logo")
    private String logo;

    @OneToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Users> users;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Department> departments;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    private List<Course> courses  = new ArrayList<>();

}
