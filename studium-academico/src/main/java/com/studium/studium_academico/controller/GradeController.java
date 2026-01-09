package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.GradeRequestDTO;
import com.studium.studium_academico.business.dto.response.AverageResult;
import com.studium.studium_academico.business.dto.response.GradeResponseDTO;
import com.studium.studium_academico.business.service.GradeFilterService;
import com.studium.studium_academico.business.service.GradeService;
import com.studium.studium_academico.infrastructure.entity.TypeGrade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final GradeFilterService filterService;

    // ==================== CRUD ENDPOINTS ====================

    @PostMapping
    public ResponseEntity<GradeResponseDTO> create(@RequestBody GradeRequestDTO dto) {
        GradeResponseDTO created = gradeService.createGrade(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody GradeRequestDTO dto
    ) {
        GradeResponseDTO updated = gradeService.updateGrade(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeResponseDTO> findById(@PathVariable UUID id) {
        GradeResponseDTO grade = gradeService.findById(id);
        return ResponseEntity.ok(grade);
    }

    // ==================== LISTING ENDPOINTS ====================

    @GetMapping
    public ResponseEntity<Page<GradeResponseDTO>> getAllGrades(Pageable pageable) {
        Page<GradeResponseDTO> grades = gradeService.findAll(pageable);
        return ResponseEntity.ok(grades);
    }

    // Endpoint simplificado para front-end - busca por turma e disciplina
    @GetMapping("/by-class-discipline")
    public ResponseEntity<List<GradeResponseDTO>> getByClassAndDiscipline(
            @RequestParam UUID classId,
            @RequestParam UUID disciplineId
    ) {
        List<GradeResponseDTO> grades = gradeService.findAllByClassAndDiscipline(classId, disciplineId);
        return ResponseEntity.ok(grades);
    }

    // Endpoint para buscar notas de um aluno específico em uma disciplina
    @GetMapping("/by-student-discipline")
    public ResponseEntity<List<GradeResponseDTO>> getByStudentAndDiscipline(
            @RequestParam UUID studentId,
            @RequestParam UUID disciplineId
    ) {
        List<GradeResponseDTO> grades = gradeService.findByStudentAndDiscipline(studentId, disciplineId);
        return ResponseEntity.ok(grades);
    }

    // Endpoint para buscar notas de uma matrícula específica
    @GetMapping("/by-registration-discipline")
    public ResponseEntity<List<GradeResponseDTO>> getByRegistrationAndDiscipline(
            @RequestParam UUID registrationId,
            @RequestParam UUID disciplineId
    ) {
        List<GradeResponseDTO> grades = gradeService.findByRegistrationAndDiscipline(registrationId, disciplineId);
        return ResponseEntity.ok(grades);
    }

    // ==================== FILTER ENDPOINTS ====================

    // Endpoint de filtro principal (mantido para compatibilidade)
    @GetMapping("/filter")
    public ResponseEntity<Page<GradeResponseDTO>> filter(
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) UUID disciplineId,
            Pageable pageable
    ) {
        Page<GradeResponseDTO> grades = gradeService.filterGrades(courseId, classId, disciplineId, pageable);
        return ResponseEntity.ok(grades);
    }

    // Endpoint específico para diretor (curso + turma + disciplina)
    @GetMapping("/filter/director")
    public ResponseEntity<Page<GradeResponseDTO>> filterForDirector(
            @RequestParam UUID courseId,
            @RequestParam UUID classId,
            @RequestParam UUID disciplineId,
            Pageable pageable
    ) {
        Page<GradeResponseDTO> grades = gradeService.filterGrades(courseId, classId, disciplineId, pageable);
        return ResponseEntity.ok(grades);
    }

    // ==================== LEGACY FILTER ENDPOINTS (mantidos para compatibilidade) ====================

    @GetMapping("/filter/student")
    public ResponseEntity<Page<GradeResponseDTO>> filterStudent(
            @RequestParam UUID studentId,
            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) TypeGrade type,
            Pageable pageable
    ) {
        Page<GradeResponseDTO> grades = filterService.filterForStudent(studentId, disciplineId, type, pageable);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/filter/teacher")
    public ResponseEntity<Page<GradeResponseDTO>> filterTeacher(
            @RequestParam UUID teacherId,
            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) TypeGrade type,
            Pageable pageable
    ) {
        Page<GradeResponseDTO> grades = filterService.filterForTeacher(teacherId, disciplineId, classId, type, pageable);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/filter/course")
    public ResponseEntity<Page<GradeResponseDTO>> filterCourse(
            @RequestParam UUID courseId,
            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) TypeGrade type,
            Pageable pageable
    ) {
        Page<GradeResponseDTO> grades = filterService.filterForCourse(courseId, disciplineId, classId, type, pageable);
        return ResponseEntity.ok(grades);
    }

    // ==================== BUSINESS ENDPOINTS ====================

    @GetMapping("/average/{registrationId}/{disciplineId}")
    public ResponseEntity<AverageResult> getStudentAverage(
            @PathVariable UUID registrationId,
            @PathVariable UUID disciplineId
    ) {
        AverageResult result = gradeService.getStudentAverage(registrationId, disciplineId);
        return ResponseEntity.ok(result);
    }
}