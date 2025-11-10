package com.studium.studium_academico.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequestDTO(
        @NotBlank(message = "Street is required")
        String street,
        @NotBlank(message = "Number is required")
        String number,
        String complement,
        @NotBlank(message = "City is required")
        String city,
        @NotBlank(message = "State is required")
        String state,
        @NotBlank(message = "CEP is required") @Pattern(regexp = "\\d{8}", message = "CEP must have 8 digits")
        String cep
) {
}
