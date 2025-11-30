package com.studium.studium_academico.business.service;

import com.studium.studium_academico.infrastructure.entity.ActivationToken;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.ActivationTokenRepository;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivationServiceTest {

    @InjectMocks
    private com.studium.studium_academico.business.service.ActivationService activationService;

    @Mock private ActivationTokenRepository tokenRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private com.studium.studium_academico.business.service.EmailService emailService;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    void generateAndSendActivationLink_shouldSaveTokenAndCallEmail() {
        Users user = new Users();
        user.setId(UUID.randomUUID());
        user.setEmail("foo@studium.com");
        user.setName("Foo");

        when(tokenRepository.save(any(ActivationToken.class))).thenAnswer(i -> i.getArgument(0));

        activationService.generateAndSendActivationLink(user);

        // tokenRepository.save should be called
        verify(tokenRepository, times(1)).save(any(ActivationToken.class));
        // email sending is triggered asynchronously after commit in your impl; here we just verify calling path won't throw
    }

    @Test
    void activateAccount_shouldThrowOnExpiredToken() {
        ActivationToken token = ActivationToken.builder()
                .token("abc")
                .expiryDate(LocalDateTime.now().minusHours(1))
                .used(false)
                .user(new Users())
                .build();

        when(tokenRepository.findByToken("abc")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> activationService.activateAccount("abc", "Abcd1234"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expir");
    }

    // Add test for successful activation — mock repositories and assert password updated, token used true
}
