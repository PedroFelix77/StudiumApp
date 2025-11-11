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
    public Address create(AddressRequestDTO data) {
        Address address = new Address();
        address.setCep(data.cep());
        address.setStreet(data.street());
        address.setNumber(data.number());
        address.setCity(data.city());
        address.setState(data.state());
        address.setComplement(data.complement());

        return repository.save(address);
    }

    public List<AddressResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(AddressResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Address findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

    @Transactional
    public Address update(UUID id, AddressRequestDTO data) {
        Address address = findById(id);
        address.setCep(data.cep());
        address.setStreet(data.street());
        address.setNumber(data.number());
        address.setCity(data.city());
        address.setState(data.state());
        address.setComplement(data.complement());

        return repository.save(address);
    }

    @Transactional
    public void delete(UUID id) {
        Address address = repository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
        repository.delete(address);
    }

}