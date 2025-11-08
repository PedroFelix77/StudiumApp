package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.request.AuthLoginRequestDTO;
import com.studium.studium_academico.business.dto.response.AuthRegisterResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsersRepository repository;
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthLoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid AuthRegisterResponseDTO data) {
        if (this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        Users user = Users.builder()
                .name(data.name())
                .cpf(data.cpf())
                .email(data.email())
                .password(encryptedPassword)
                .birthday(data.birthday())
                .phone(data.phone())
                .role(data.role()) // enum UserRole
                .build();

        repository.save(user);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }
}
