package com.studium.studium_academico.business.dto.request;

public record CreateStudentRequestDTO(
        UserRequestDTO user,
        AddressRequestDTO address,
        String responsibleName,
        String responsiblePhone
) {
}
