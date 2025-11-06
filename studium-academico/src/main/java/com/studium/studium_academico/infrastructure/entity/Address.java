package com.studium.studium_academico.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Address extends BaseEntity {
    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "complement")
    private String complement;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "cep",  nullable = false)
    private String cep;

    @OneToOne(mappedBy = "address",  fetch = FetchType.EAGER)
    private Institution institution;

    @OneToMany(mappedBy = "address",fetch = FetchType.LAZY)
    private List<Users> users = new ArrayList<>();

}
