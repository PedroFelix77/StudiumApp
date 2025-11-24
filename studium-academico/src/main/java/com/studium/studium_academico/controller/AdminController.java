package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.CreateAdminRequestDTO;
import com.studium.studium_academico.business.dto.request.CreateDirectorRequestDTO;
import com.studium.studium_academico.business.dto.response.AdminResponseDTO;
import com.studium.studium_academico.business.dto.response.DirectorResponseDTO;
import com.studium.studium_academico.business.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/directors")
    public ResponseEntity<DirectorResponseDTO> createDirector(
            @RequestBody CreateDirectorRequestDTO request
    ) {
        DirectorResponseDTO response = adminService.createDirector(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody @Valid CreateAdminRequestDTO data) {
        AdminResponseDTO response = adminService.createAdmin(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
