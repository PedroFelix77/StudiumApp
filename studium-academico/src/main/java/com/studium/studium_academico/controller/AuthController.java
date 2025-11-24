package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.AuthLoginRequestDTO;
import com.studium.studium_academico.business.dto.request.PasswordActivationDTO;
import com.studium.studium_academico.business.dto.response.AuthLoginResponseDTO;
import com.studium.studium_academico.business.service.ActivationService;
import com.studium.studium_academico.business.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final ActivationService activationService;

    public AuthController(AuthService authService, ActivationService activationService) {
        this.authService = authService;
        this.activationService = activationService;
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

}
