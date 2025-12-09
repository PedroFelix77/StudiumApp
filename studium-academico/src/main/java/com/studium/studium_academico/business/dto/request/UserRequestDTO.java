package com.studium.studium_academico.business.dto.request;

import com.studium.studium_academico.infrastructure.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.UUID;

public record UserRequestDTO(
        @NotBlank String name,
        @NotBlank @CPF String cpf,
        @NotBlank @Email String email,
        @NotNull LocalDate birthday,
        @NotBlank String phone,
        UUID institutionId
) {
}
