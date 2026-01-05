package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.TeacherClassCreateDTO;
import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.business.service.TeacherClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teacher-classes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DIRECTOR') or hasRole('ADMIN')")
public class TeacherClassesController {

    private final TeacherClassService service;

    @PostMapping
    public ResponseEntity<TeacherClassResponseDTO> create(
            @RequestBody @Valid TeacherClassCreateDTO dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public List<TeacherClassResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/teacher/{teacherId}")
    public List<TeacherClassResponseDTO> findByTeacher(
            @PathVariable UUID teacherId
    ) {
        return service.findByTeacher(teacherId);
    }

    @GetMapping("/teacher/{teacherId}/course/{courseId}")
    public List<TeacherClassResponseDTO> findByTeacherAndCourse(
            @PathVariable UUID teacherId,
            @PathVariable UUID courseId
    ) {
        return service.findByTeacherAndCourse(teacherId, courseId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/class/{classId}")
    public List<TeacherClassResponseDTO> findByClass(
            @PathVariable UUID classId
    ) {
        return service.findByClass(classId);
    }
}
