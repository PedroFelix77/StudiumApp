package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.FrequencyByRegistrationRequestDTO;
import com.studium.studium_academico.business.dto.request.FrequencyRequestDTO;
import com.studium.studium_academico.business.dto.response.ClassroomResponseDTO;
import com.studium.studium_academico.business.dto.response.FrequencyResponseDTO;
import com.studium.studium_academico.business.service.FrequencyService;
import com.studium.studium_academico.business.service.FrequencyFilterService;
import com.studium.studium_academico.infrastructure.entity.StatusFrequency;
import com.studium.studium_academico.infrastructure.entity.Users;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/frequencies")
@RequiredArgsConstructor
public class FrequencyController {

    private final FrequencyService frequencyService;
    private final FrequencyFilterService filterService;

    @PostMapping
    public FrequencyResponseDTO create(@RequestBody FrequencyRequestDTO dto) {
        return frequencyService.createFrequency(dto);
    }

    @PutMapping("/{id}")
    public FrequencyResponseDTO update(
            @PathVariable UUID id,
            @RequestBody FrequencyRequestDTO dto
    ) {
        return frequencyService.updateFrequency(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        frequencyService.deleteFrequency(id);
    }

    @GetMapping("/{id}")
    public FrequencyResponseDTO findById(@PathVariable UUID id) {
        return frequencyService.findById(id);
    }

    // FILTRO GLOBAL — Admin/Supervisor (todos os perfis)
    @GetMapping("/filter")
    public ResponseEntity<Page<FrequencyResponseDTO>> filterGlobal(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID registrationId,
            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) UUID teacherId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) UUID classroomId,
            @RequestParam(required = false) StatusFrequency status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @PageableDefault(size = 20, sort = "attendanceDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<FrequencyResponseDTO> result = filterService.filterGlobal(
                studentId, registrationId, disciplineId, courseId,
                teacherId, classId, classroomId, status,
                startDate, endDate, pageable
        );
        return ResponseEntity.ok(result);
    }

    // FILTRO — Aluno (próprias frequências)
    @GetMapping("/filter/student")
    @PreAuthorize("hasRole('STUDENT') and #studentId == authentication.principal.id")
    public ResponseEntity<Page<FrequencyResponseDTO>> filterByStudent(
            @RequestParam @NotNull UUID studentId,

            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) StatusFrequency status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @PageableDefault(size = 50, sort = "attendanceDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<FrequencyResponseDTO> result = filterService.filterForStudent(
                studentId, disciplineId, status, startDate, endDate, pageable
        );
        return ResponseEntity.ok(result);
    }

    // FILTRO — Professor (suas turmas)
    @GetMapping("/filter/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Page<FrequencyResponseDTO>> filterByTeacher(
            @RequestParam @NotNull UUID teacherId,

            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) UUID classroomId,
            @RequestParam(required = false) StatusFrequency status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @PageableDefault(size = 30, sort = "attendanceDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {

        Page<FrequencyResponseDTO> result = filterService.filterForTeacher(
                teacherId, disciplineId, classId, classroomId,
                status, startDate, endDate, pageable
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/filter/course")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<Page<FrequencyResponseDTO>> filterByCourse(
            @RequestParam @NotNull UUID courseId,

            @RequestParam(required = false) UUID disciplineId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) StatusFrequency status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @PageableDefault(size = 30, sort = "attendanceDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<FrequencyResponseDTO> result = filterService.filterForCourse(
                courseId, disciplineId, classId, status, startDate, endDate, pageable
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/by-registration")
    @PreAuthorize("hasAnyRole('TEACHER', 'DIRECTOR')")
    public ResponseEntity<FrequencyResponseDTO> createByRegistration(
            @RequestBody FrequencyByRegistrationRequestDTO dto
    ) {
        FrequencyResponseDTO response =
                frequencyService.createFrequencyByRegistration(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
