package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.CourseRequestDTO;
import com.studium.studium_academico.business.dto.request.CourseUpdateRequestDTO;
import com.studium.studium_academico.business.dto.response.CourseResponseDTO;
import com.studium.studium_academico.business.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponseDTO> create(@Valid @RequestBody CourseRequestDTO req) {
        CourseResponseDTO dto = courseService.createCourse(req);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> get(@PathVariable UUID id) {
        CourseResponseDTO dto = courseService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> list(Pageable pageable) {
        Page<CourseResponseDTO> page = courseService.list(pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody CourseUpdateRequestDTO data) {
        CourseResponseDTO dto = courseService.update(id, data);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<CourseResponseDTO>> getByTeacher(
            @PathVariable UUID teacherId
    ) {
        return ResponseEntity.ok(courseService.findByTeacher(teacherId));
    }

}
