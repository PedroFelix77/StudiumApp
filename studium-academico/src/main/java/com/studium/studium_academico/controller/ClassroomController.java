package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.ClassroomRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassroomResponseDTO;
import com.studium.studium_academico.business.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService service;

    @PostMapping
    public ClassroomResponseDTO create(@RequestBody ClassroomRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public Page<ClassroomResponseDTO> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ClassroomResponseDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ClassroomResponseDTO update(
            @PathVariable UUID id,
            @RequestBody ClassroomRequestDTO dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
