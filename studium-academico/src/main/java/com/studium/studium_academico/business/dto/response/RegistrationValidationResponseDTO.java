package com.studium.studium_academico.business.dto.response;

import java.util.List;

public record RegistrationValidationResponseDTO(
        boolean canDelete,
        List<String> blockers
) {}
