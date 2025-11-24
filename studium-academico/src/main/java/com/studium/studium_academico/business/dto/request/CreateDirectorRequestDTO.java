package com.studium.studium_academico.business.dto.request;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public record CreateDirectorRequestDTO(
        UserRequestDTO user,
        AddressRequestDTO address,
        LocalDate hireDate
) {
}
