package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.AddressRequestDTO;
import com.studium.studium_academico.infrastructure.entity.Address;
import com.studium.studium_academico.infrastructure.repository.AddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {
    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Address create(AddressRequestDTO data) {
        Address address = Address.builder()
                .street(data.street())
                .number(data.number())
                .complement(data.complement())
                .city(data.city())
                .state(data.state())
                .cep(data.cep())
                .build();
        return repository.save(address);
    }

    public Address findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

    @Transactional
    public Address update(UUID id, AddressRequestDTO data) {
        Address address = findById(id);
        address.setStreet(data.street());
        address.setNumber(data.number());
        address.setComplement(data.complement());
        address.setCity(data.city());
        address.setState(data.state());
        address.setCep(data.cep());
        return repository.save(address);
    }

    @Transactional
    public void delete(UUID id) { repository.deleteById(id); }

}