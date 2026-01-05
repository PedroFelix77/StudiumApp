package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.response.StudentResponseDTO;
import com.studium.studium_academico.business.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * GET /students?q=texto&page=0&size=20&sort=user.name,asc
     * Acesso: DIRECTOR e ADMIN (conforme sua regra — ajuste a expressão se quiser mais roles)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    public ResponseEntity<Page<StudentResponseDTO>> list(
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        Page<StudentResponseDTO> page = studentService.list(q, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * GET /students/{id}
     * Acesso: DIRECTOR/ADMIN; também permita STUDENT ver seu próprio recurso no front (se quiser)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<StudentResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        studentService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

}
