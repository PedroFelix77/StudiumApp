package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.CreateStudentRequestDTO;
import com.studium.studium_academico.business.dto.request.CreateTeacherRequestDTO;
import com.studium.studium_academico.business.dto.response.StudentResponseDTO;
import com.studium.studium_academico.business.dto.response.TeacherResponseDTO;
import com.studium.studium_academico.business.service.DirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/director")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping("/teachers")
    public ResponseEntity<TeacherResponseDTO> createTeacher(
            @RequestBody @Valid CreateTeacherRequestDTO data
    ) {
        TeacherResponseDTO response = directorService.createTeacher(data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/students")
    public ResponseEntity<StudentResponseDTO> createStudent(
            @RequestBody @Valid CreateStudentRequestDTO data
    ) {
        StudentResponseDTO response = directorService.createStudent(data);
        return ResponseEntity.ok(response);
    }

}
