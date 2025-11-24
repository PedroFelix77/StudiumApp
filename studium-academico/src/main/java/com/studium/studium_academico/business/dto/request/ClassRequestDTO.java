package com.studium.studium_academico.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClassRequestDTO(
        @NotBlank(message = "Nome da turma é obrigatório")
        String name,

        @NotBlank(message = "Código da turma é obrigatório")
        String code_class,

        @NotBlank(message = "Ano acadêmico é obrigatório")
        String academicYear,

        @NotNull(message = "Curso é obrigatório")
        UUID courseId
) {}