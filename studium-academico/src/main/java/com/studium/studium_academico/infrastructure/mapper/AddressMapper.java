package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.response.AddressResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Address;
import org.springframework.stereotype.Component;

@Component("addressMapper")
public class AddressMapper {

    public AddressResponseDTO toResponseDTO(Address address) {
        if (address == null) return null;

        return new AddressResponseDTO(
                address.getId(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getCity(),
                address.getState(),
                address.getCep()
        );
    }
}