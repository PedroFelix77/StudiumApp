package com.studium.studium_academico.business.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.UUID;

public record CreateAdminRequestDTO(
        @NotBlank String name,
        @NotBlank @CPF String cpf,
        @NotBlank @Email String email,
        @NotNull LocalDate birthday,
        @NotBlank String phone,
        @NotBlank String password,
        @NotNull UUID institutionId,
        AddressRequestDTO address,
        LocalDate hireDate
) {}

