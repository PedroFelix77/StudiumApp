package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.ClassRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassResponseDTO;
import com.studium.studium_academico.business.service.ClassesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classes")
public class ClassesController {

    private final ClassesService service;

    @PostMapping
    public ClassResponseDTO create(@RequestBody ClassRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public Page<ClassResponseDTO> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ClassResponseDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ClassResponseDTO update(@PathVariable UUID id, @RequestBody ClassRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
