package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.ClassRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassResponseDTO;
import com.studium.studium_academico.business.service.ClassesService;
import com.studium.studium_academico.infrastructure.entity.Classes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/classes")
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

    @GetMapping("/teacher/{teacherId}/course/{courseId}")
    public ResponseEntity<List<ClassResponseDTO>> getClassesByTeacherAndCourse(
            @PathVariable UUID teacherId,
            @PathVariable UUID courseId
    ) {
        List<Classes> classes =
                service.findClassesByTeacherAndCourse(teacherId, courseId);

        List<ClassResponseDTO> response = classes.stream()
                .map(c -> new ClassResponseDTO(
                        c.getId(),
                        c.getName(),
                        c.getCodeClass(),
                        c.getAcademicYear()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ClassResponseDTO update(@PathVariable UUID id, @RequestBody ClassRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/course/{courseId}")
    public List<ClassResponseDTO> findByCourse(@PathVariable UUID courseId) {
        return service.findByCourseId(courseId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
