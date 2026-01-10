package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.TeacherClassCreateDTO;
import com.studium.studium_academico.business.dto.response.ClassResponseDTO;
import com.studium.studium_academico.business.dto.response.DisciplineResponseDTO;
import com.studium.studium_academico.business.dto.response.TeacherClassResponseDTO;
import com.studium.studium_academico.business.service.TeacherClassService;
import com.studium.studium_academico.infrastructure.entity.Users;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teacher-classes")
@RequiredArgsConstructor
public class TeacherClassesController {

    private final TeacherClassService service;

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TeacherClassResponseDTO> create(
            @RequestBody @Valid TeacherClassCreateDTO dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('ADMIN')")
    @GetMapping
    public List<TeacherClassResponseDTO> findAll() {
        return service.findAll();
    }

    // 👇 PROFESSOR PODE ACESSAR
    @PreAuthorize("hasRole('TEACHER') or hasRole('DIRECTOR') or hasRole('ADMIN')")
    @GetMapping("/teacher/{teacherId}/classes")
    public ResponseEntity<List<ClassResponseDTO>> getClassesByTeacher(
            @PathVariable UUID teacherId
    ) {
        return ResponseEntity.ok(service.findClassesByTeacher(teacherId));
    }

    // 👇 PROFESSOR PODE ACESSAR
    @PreAuthorize("hasRole('TEACHER') or hasRole('DIRECTOR') or hasRole('ADMIN')")
    @GetMapping("/teacher/{teacherId}/disciplines")
    public ResponseEntity<List<DisciplineResponseDTO>> getDisciplinesByTeacherAndClass(
            @PathVariable UUID teacherId,
            @RequestParam UUID classId
    ) {
        return ResponseEntity.ok(
                service.findDisciplinesByTeacherAndClass(teacherId, classId)
        );
    }

    @GetMapping("/my/disciplines")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<DisciplineResponseDTO>> getMyDisciplines(
            @RequestParam UUID classId
    ) {
        Users user = (Users) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(
                service.findDisciplinesByLoggedTeacherAndClass(user.getId(), classId)
        );
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my/classes")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<ClassResponseDTO>> getMyClasses() {

        Users user = (Users) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(
                service.findClassesByLoggedTeacher(user.getId())
        );
    }
}
