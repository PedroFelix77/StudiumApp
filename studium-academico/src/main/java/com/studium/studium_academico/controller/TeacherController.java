package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.response.TeacherResponseDTO;
import com.studium.studium_academico.business.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN')")
    public ResponseEntity<Page<TeacherResponseDTO>> list(
            @RequestParam(required = false) String q,
            Pageable pageable
    ) {
        return ResponseEntity.ok(teacherService.list(q, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTOR','ADMIN','TEACHER')")
    public ResponseEntity<TeacherResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teacherService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

}
