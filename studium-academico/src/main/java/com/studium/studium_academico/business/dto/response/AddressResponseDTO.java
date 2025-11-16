package com.studium.studium_academico.business.dto.response;


import com.studium.studium_academico.infrastructure.entity.Address;

import java.util.UUID;

public record AddressResponseDTO(
        UUID id,
        String cep,
        String street,
        String number,
        String city,
        String state,
        String complement
) {
    public AddressResponseDTO(Address address) {
        this(
                address.getId(),
                address.getCep(),
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                address.getComplement()
        );
    }
}
