package com.studium.studium_academico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studium.studium_academico.business.service.ActivationService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class ActivationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private ActivationService activationService;

    @Test
    void activate_shouldReturnOk() throws Exception {
        var body = Map.of("token", "abc", "newPassword", "Abcd1234");

        mockMvc.perform(post("/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(activationService, times(1)).activateAccount("abc", "Abcd1234");
    }

    @Test
    void resendEndpointShouldCallService() throws Exception {
        mockMvc.perform(post("/auth/resend/someone@studium.com"))
                .andExpect(status().isOk());

        verify(activationService, times(1)).resendActivationLink("someone@studium.com");
    }
}
