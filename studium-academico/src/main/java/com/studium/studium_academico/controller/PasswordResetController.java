package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.ForgotPasswordRequestDTO;
import com.studium.studium_academico.business.dto.request.PasswordResetRequestDTO;
import com.studium.studium_academico.business.dto.response.ForgotPasswordResponseDTO;
import com.studium.studium_academico.business.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {

        var resetToken = passwordResetService.generateResetToken(request.email());

        return ResponseEntity.ok(
                new ForgotPasswordResponseDTO(
                        "Um e-mail com instruções foi enviado.",
                        null // o front não precisa do link, ele vem por e-mail
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody PasswordResetRequestDTO request) {


        passwordResetService.resetPassword(request.token(), request.newPassword());

        return ResponseEntity.ok("Senha alterada com sucesso.");
    }

    @PostMapping("/resend-reset")
    public ResponseEntity<String> resendReset(@RequestBody Map<String, String> body) {
        String oldToken = body.get("token");

        passwordResetService.resendResetToken(oldToken);

        return  ResponseEntity.ok("Novo link de redefinição enviado");
    }
}

