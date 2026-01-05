package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.AuthLoginRequestDTO;
import com.studium.studium_academico.business.dto.request.ForgotPasswordRequestDTO;
import com.studium.studium_academico.business.dto.request.PasswordActivationDTO;
import com.studium.studium_academico.business.dto.request.PasswordResetRequestDTO;
import com.studium.studium_academico.business.dto.response.AuthLoginResponseDTO;
import com.studium.studium_academico.business.dto.response.ForgotPasswordResponseDTO;
import com.studium.studium_academico.business.service.ActivationService;
import com.studium.studium_academico.business.service.AuthService;
import com.studium.studium_academico.business.service.PasswordResetService;
import com.studium.studium_academico.infrastructure.entity.PasswordResetToken;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final ActivationService activationService;
    private final PasswordResetService passwordResetService;
    @Value("${frontend.url}")
    private String frontEndUrl;

    public AuthController(AuthService authService, ActivationService activationService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.activationService = activationService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponseDTO> login(@RequestBody @Valid AuthLoginRequestDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestBody PasswordActivationDTO data) {
        activationService.activateAccount(data.token(), data.newPassword());
        return ResponseEntity.ok("Conta ativada com sucesso.");
    }

    @PostMapping("/resend/{email}")
    public ResponseEntity<String> resend(@PathVariable String email){
        activationService.resendActivationLink(email);
        return ResponseEntity.ok("Novo link enviado com sucesso");
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resend(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        activationService.resendActivationLinkByToken(token);
        return ResponseEntity.ok("Novo link enviado com sucesso");
    }

}
