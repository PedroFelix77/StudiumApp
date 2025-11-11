package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.AddressRequestDTO;
import com.studium.studium_academico.business.dto.response.AddressResponseDTO;
import com.studium.studium_academico.business.service.AddressService;
import com.studium.studium_academico.infrastructure.entity.Address;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/address")
public class AddressController {
    private final AddressService service;
    public AddressController(AddressService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(@RequestBody @Valid AddressRequestDTO data) {
        Address saved = service.create(data);
        AddressResponseDTO response = new AddressResponseDTO(saved);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> getAddress(@PathVariable UUID id) {
        Address address = service.findById(id);
        AddressResponseDTO response = new AddressResponseDTO(address);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable UUID id, @RequestBody @Valid AddressRequestDTO data) {
        Address updated = service.update(id, data);
        AddressResponseDTO response = new AddressResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
