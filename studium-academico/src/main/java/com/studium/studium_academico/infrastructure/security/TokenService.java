package com.studium.studium_academico.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.studium.studium_academico.infrastructure.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    public String generateToken(Users users) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("studium")
                    .withSubject(users.getEmail())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
            return token;
        }catch (JWTCreationException exception){
            log.error("Erro ao gerar token para usuário: {}", users.getEmail(), exception);
            throw new RuntimeException("Error while creating JWT",  exception);
        }
    }

    public String validateToken(String token){

        if (token == null || token.trim().isEmpty()) {
            log.warn("Token vazio recebido para validação");
            return null;
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String email = JWT.require(algorithm)
                    .withIssuer("studium")
                    .build()
                    .verify(token)
                    .getSubject();

            //garante q nao retorna email vazio
            return (email != null && !email.trim().isEmpty()) ? email : null;
        }catch (JWTVerificationException exception){
            log.warn("Token inválido ou expirado: {}", exception.getMessage());
            return null;
        }
    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
