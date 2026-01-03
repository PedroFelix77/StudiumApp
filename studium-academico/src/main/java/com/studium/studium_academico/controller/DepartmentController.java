package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.DepartmentRequestDTO;
import com.studium.studium_academico.business.dto.response.DepartmentResponseDTO;
import com.studium.studium_academico.business.dto.response.DepartmentWithStatsResponseDTO;
import com.studium.studium_academico.business.service.DepartmentService;
import com.studium.studium_academico.infrastructure.entity.Department;
import com.studium.studium_academico.infrastructure.repository.DepartmentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(
            @RequestBody @Valid DepartmentRequestDTO data) {
        DepartmentResponseDTO departmentResponseDTO = departmentService.createDepartment(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        List<DepartmentResponseDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartment(@PathVariable UUID id) {
        DepartmentResponseDTO department = departmentService.findById(id);
        return ResponseEntity.ok(department);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(
            @PathVariable UUID id,
            @RequestBody @Valid DepartmentRequestDTO data) {
        DepartmentResponseDTO departmentResponseDTO = departmentService.updateDepartment(id, data);
        return ResponseEntity.ok(departmentResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/with-stats")
    public ResponseEntity<List<DepartmentWithStatsResponseDTO>> getDepartmentsWithStats() {
        List<DepartmentWithStatsResponseDTO> departments = departmentService.findAllWithStats();
        return ResponseEntity.ok(departments);
    }
}

