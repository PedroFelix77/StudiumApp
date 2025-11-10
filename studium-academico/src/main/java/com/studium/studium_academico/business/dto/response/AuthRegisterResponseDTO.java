package com.studium.studium_academico.business.dto.response;

import com.studium.studium_academico.business.dto.request.AddressRequestDTO;
import com.studium.studium_academico.infrastructure.entity.Institution;
import com.studium.studium_academico.infrastructure.entity.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


public record AuthRegisterResponseDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String name,
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        String email,
        @NotNull(message = "O tipo de usuário (role) é obrigatório.")
        UserRole role
        ) {}
