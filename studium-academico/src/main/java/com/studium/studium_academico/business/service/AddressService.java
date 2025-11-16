package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.AddressRequestDTO;
import com.studium.studium_academico.business.dto.response.AddressResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Address;
import com.studium.studium_academico.infrastructure.entity.EntityStatus;
import com.studium.studium_academico.infrastructure.repository.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressService {
    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public Address createEntity(AddressRequestDTO data) {
        Address address = Address.builder()
                .cep(data.cep())
                .street(data.street())
                .number(data.number())
                .complement(data.complement())
                .city(data.city())
                .state(data.state())
                .build();

        return repository.save(address);
    }

    @Transactional
    public AddressResponseDTO create(AddressRequestDTO data) {
        Address address = createEntity(data);
        return new AddressResponseDTO(address);
    }

    public List<AddressResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(AddressResponseDTO::new)
                .collect(Collectors.toList());
    }

    public AddressResponseDTO findById(UUID id) {
        Address address = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        return new AddressResponseDTO(address);
    }

    @Transactional
    public AddressResponseDTO update(UUID id, AddressRequestDTO data) {
        Address address = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        address.setCep(data.cep());
        address.setStreet(data.street());
        address.setNumber(data.number());
        address.setComplement(data.complement());
        address.setCity(data.city());
        address.setState(data.state());

        Address updated = repository.save(address);
        return new AddressResponseDTO(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Address address = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        repository.delete(address);
    }

}