package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.AuthLoginRequestDTO;
import com.studium.studium_academico.business.dto.response.AuthLoginResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.security.TokenService;
import com.studium.studium_academico.infrastructure.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private com.studium.studium_academico.business.service.AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginShouldReturnTokenAndUser_whenCredentialsValid() {
        AuthLoginRequestDTO dto = new AuthLoginRequestDTO("admin@studium.com", "password");
        Users user = Users.builder()
                .email("admin@studium.com")
                .name("Admin")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        when(tokenService.generateToken(user)).thenReturn("jwt-token");
        when(userMapper.toResponseDTO(user)).thenReturn(
                new com.studium.studium_academico.business.dto.response.UserResponseDTO(
                        user.getId(), user.getName(), user.getEmail(), null, null, null
                )
        );

        AuthLoginResponseDTO result = authService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.user()).isNotNull();
        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenService, times(1)).generateToken(user);
    }

    // Add tests for authentication failure if desired (mock authenticationManager to throw)
}
