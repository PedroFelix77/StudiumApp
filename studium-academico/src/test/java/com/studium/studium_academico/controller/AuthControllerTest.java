package com.studium.studium_academico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studium.studium_academico.business.dto.request.AuthLoginRequestDTO;
import com.studium.studium_academico.business.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private AuthService authService;

    @BeforeEach void setup(){ MockitoAnnotations.openMocks(this); }

    @Test
    void loginEndpointShouldReturn200AndToken() throws Exception {
        var req = new AuthLoginRequestDTO("admin@studium.com", "password");

        // mock service response
        var userResp = new com.studium.studium_academico.business.dto.response.UserResponseDTO(
                java.util.UUID.randomUUID(), "Admin", "admin@studium.com", null, null, null
        );
        var authResp = new com.studium.studium_academico.business.dto.response.AuthLoginResponseDTO("jwt-token", userResp);

        when(authService.login(any())).thenReturn(authResp);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.email").value("admin@studium.com"));

        verify(authService, times(1)).login(any());
    }
}
