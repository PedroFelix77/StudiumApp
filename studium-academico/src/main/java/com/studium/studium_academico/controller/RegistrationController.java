package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.RegistrationRequestDTO;
import com.studium.studium_academico.business.dto.response.RegistrationResponseDTO;
import com.studium.studium_academico.business.dto.response.RegistrationValidationResponseDTO;
import com.studium.studium_academico.business.dto.response.StudentInClassResponseDTO;
import com.studium.studium_academico.business.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // CREATE
    @PostMapping()
    public ResponseEntity<RegistrationResponseDTO> createRegistration(@RequestBody RegistrationRequestDTO data) {
        RegistrationResponseDTO response = registrationService.createRegistration(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponseDTO> getRegistration(@PathVariable UUID id) {
        RegistrationResponseDTO response = registrationService.findById(id);
        return ResponseEntity.ok(response);
    }

    // READ BY REGISTRATION NUMBER
    @GetMapping("/number/{registrationNumber}")
    public ResponseEntity<RegistrationResponseDTO> getRegistrationByNumber(@PathVariable String registrationNumber) {
        RegistrationResponseDTO response = registrationService.findByRegistrationNumber(registrationNumber);
        return ResponseEntity.ok(response);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<RegistrationResponseDTO>> getAllRegistrations() {
        List<RegistrationResponseDTO> registrations = registrationService.findAllRegistrations();
        return ResponseEntity.ok(registrations);
    }

    // READ BY STUDENT
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RegistrationResponseDTO>> getStudentRegistrations(@PathVariable UUID studentId) {
        List<RegistrationResponseDTO> registrations = registrationService.findByStudentId(studentId);
        return ResponseEntity.ok(registrations);
    }

    // READ BY COURSE
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<RegistrationResponseDTO>> getCourseRegistrations(@PathVariable UUID courseId) {
        List<RegistrationResponseDTO> registrations = registrationService.findByCourseId(courseId);
        return ResponseEntity.ok(registrations);
    }

    // READ BY CLASS
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<RegistrationResponseDTO>> getClassRegistrations(@PathVariable UUID classId) {
        List<RegistrationResponseDTO> registrations = registrationService.findByClassEntityId(classId);
        return ResponseEntity.ok(registrations);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<RegistrationResponseDTO> updateRegistration(@PathVariable UUID id, @RequestBody RegistrationRequestDTO data) {
        RegistrationResponseDTO response = registrationService.updateRegistration(id, data);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable UUID id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }

    // VALIDATE DELETION
    @GetMapping("/{id}/validate-deletion")
    public ResponseEntity<RegistrationValidationResponseDTO> validateRegistrationDeletion(@PathVariable UUID id) {
        RegistrationValidationResponseDTO validation = registrationService.validateDeletion(id);
        return ResponseEntity.ok(validation);
    }

    // COUNT BY STUDENT
    @GetMapping("/student/{studentId}/count")
    public ResponseEntity<Long> countStudentRegistrations(@PathVariable UUID studentId) {
        Long count = registrationService.countByStudentId(studentId);
        return ResponseEntity.ok(count);
    }

    // COUNT BY COURSE
    @GetMapping("/course/{courseId}/count")
    public ResponseEntity<Long> countCourseRegistrations(@PathVariable UUID courseId) {
        Long count = registrationService.countByCourseId(courseId);
        return ResponseEntity.ok(count);
    }

    // COUNT BY CLASS
    @GetMapping("/class/{classId}/count")
    public ResponseEntity<Long> countClassRegistrations(@PathVariable UUID classId) {
        Long count = registrationService.countByClassId(classId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/class/{classId}/students")
    public ResponseEntity<List<StudentInClassResponseDTO>> getStudentsByClass(
            @PathVariable UUID classId
    ) {
        return ResponseEntity.ok(
                registrationService.findStudentsByClassId(classId)
        );
    }
}