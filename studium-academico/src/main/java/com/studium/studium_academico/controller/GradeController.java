package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.GradeRequestDTO;
import com.studium.studium_academico.business.dto.response.GradeResponseDTO;
import com.studium.studium_academico.business.service.GradeFilterService;
import com.studium.studium_academico.business.service.GradeService;
import com.studium.studium_academico.infrastructure.entity.Grade;
import com.studium.studium_academico.infrastructure.entity.TypeGrade;
import com.studium.studium_academico.infrastructure.mapper.GradeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final GradeFilterService filterService;
    private final GradeMapper gradeMapper;

    @PostMapping
    public GradeResponseDTO create(@RequestBody GradeRequestDTO dto) {
        return gradeService.createGrade(dto);
    }

    @PutMapping("/{id}")
    public GradeResponseDTO update(@PathVariable UUID id, @RequestBody GradeRequestDTO dto) {
        return gradeService.updateGrade(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        gradeService.deleteGrade(id);
    }

    @GetMapping("/{id}")
    public GradeResponseDTO findById(@PathVariable UUID id) {
        return gradeService.findById(id);
    }

    // GLOBAL
    @GetMapping("/filter")
    public Page<GradeResponseDTO> filterGlobal(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID registrationId,
            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) UUID teacherId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) TypeGrade type,
            Pageable pageable
    ) {
        return filterService.filterGlobal(
                studentId, registrationId, disciplineId, courseId,
                teacherId, classId, type, pageable
        );
    }

    // ALUNO
    @GetMapping("/filter/student")
    public Page<GradeResponseDTO> filterStudent(
            @RequestParam UUID studentId,
            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) TypeGrade type,
            Pageable pageable
    ) {
        return filterService.filterForStudent(studentId, disciplineId, type, pageable);
    }

    // PROFESSOR
    @GetMapping("/filter/teacher")
    public Page<GradeResponseDTO> filterTeacher(
            @RequestParam UUID teacherId,
            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) TypeGrade type,
            Pageable pageable
    ) {
        return filterService.filterForTeacher(teacherId, disciplineId, classId, type, pageable);
    }

//    @GetMapping("/filter/teacher")
//    public ResponseEntity<Page<GradeResponseDTO>> getGradesByTeacher(
//            @RequestParam UUID teacherId,
//            Pageable pageable
//    ) {
//        Page<Grade> grades = filterService.findGradesByTeacher(teacherId, pageable);
//
//        return ResponseEntity.ok(
//                grades.map(gradeMapper::toDTO)
//        );
//    }

    // DIRETOR / COORDENAÇÃO
    @GetMapping("/filter/course")
    public Page<GradeResponseDTO> filterCourse(
            @RequestParam UUID courseId,
            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) TypeGrade type,
            Pageable pageable
    ) {
        return filterService.filterForCourse(courseId, disciplineId, classId, type, pageable);
    }
}
