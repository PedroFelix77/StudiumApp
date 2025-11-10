package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.AddressRequestDTO;
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
    public ResponseEntity<Address> createAddress(@RequestBody AddressRequestDTO data) {
        Address saved = service.create(data);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddress(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable UUID id, @RequestBody @Valid AddressRequestDTO data) {
        return ResponseEntity.ok(service.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
