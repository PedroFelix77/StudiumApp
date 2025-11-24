package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.service.PasswordResetService;
import com.studium.studium_academico.infrastructure.entity.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request-reset")
    public ResponseEntity<String> requestReset(@RequestParam String email) {

        PasswordResetToken token = passwordResetService.generateResetToken(email);

        // Aqui futuramente você enviará o link por email
        // Ex: https://seusite/reset-password?token=xxxxx

        return ResponseEntity.ok("Token de reset gerado e enviado para o email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        passwordResetService.resetPassword(token, newPassword);

        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}
