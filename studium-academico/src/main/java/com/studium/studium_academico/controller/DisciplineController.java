package com.studium.studium_academico.controller;


import com.studium.studium_academico.business.dto.request.DisciplineRequestDTO;
import com.studium.studium_academico.business.dto.request.DisciplineUpdateRequestDTO;
import com.studium.studium_academico.business.dto.response.DisciplineResponseDTO;
import com.studium.studium_academico.business.service.DisciplineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/disciplines")
@RequiredArgsConstructor
public class DisciplineController {

    private final DisciplineService service;

    @PostMapping
    public ResponseEntity<DisciplineResponseDTO> create(
            @RequestBody @Valid DisciplineRequestDTO dto) {

        return ResponseEntity.ok(service.createDiscipline(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisciplineResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody DisciplineUpdateRequestDTO dto) {
        return ResponseEntity.ok(service.updateDiscipline(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisciplineResponseDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

