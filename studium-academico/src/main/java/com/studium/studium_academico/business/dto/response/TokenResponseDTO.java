package com.studium.studium_academico.business.dto.response;

public record TokenResponseDTO (
        String token,
        String email,
        String role
){
}
