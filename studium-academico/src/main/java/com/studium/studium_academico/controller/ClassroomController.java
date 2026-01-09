package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.ClassroomRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassResponseDTO;
import com.studium.studium_academico.business.dto.response.ClassroomResponseDTO;
import com.studium.studium_academico.business.dto.response.DisciplineResponseDTO;
import com.studium.studium_academico.business.service.ClassroomService;
import com.studium.studium_academico.infrastructure.entity.Classroom;
import com.studium.studium_academico.infrastructure.mapper.ClassroomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService service;
    private final ClassroomMapper mapper;

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

    @GetMapping("/by-class-and-discipline")
    public ClassroomResponseDTO getByClassAndDiscipline(
            @RequestParam UUID classId,
            @RequestParam UUID disciplineId
    ) {
        Classroom classroom = service.findByClassAndDiscipline(classId, disciplineId);
        return mapper.toResponse(classroom);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<ClassroomResponseDTO> findByTeacher(
            @PathVariable UUID teacherId
    ) {
        return service.findByTeacher(teacherId);
    }
}
