package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.CreateDirectorRequestDTO;
import com.studium.studium_academico.business.dto.request.UserRequestDTO;
import com.studium.studium_academico.business.dto.response.DirectorResponseDTO;
import com.studium.studium_academico.infrastructure.entity.UserRole;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.DirectorRepository;
import com.studium.studium_academico.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private com.studium.studium_academico.business.service.AdminService adminService;

    @Mock private com.studium.studium_academico.business.service.UserService userService;
    @Mock private DirectorRepository directorRepository;
    @Mock private UserMapper userMapper;


    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    void createDirectorShouldReturnDto_whenOk() {
        // prepare request DTO
        CreateDirectorRequestDTO request = new CreateDirectorRequestDTO(
                new UserRequestDTO("João", "11122233344", "joao@studium.com", LocalDate.of(1982,8,20), "11999997777", UUID.randomUUID()),
                null,
                LocalDate.of(2025, 1, 1)
        );

        // mock user service createUser -> returns UserResponseDTO with id
        com.studium.studium_academico.business.dto.response.UserResponseDTO userResp =
                new com.studium.studium_academico.business.dto.response.UserResponseDTO(
                        UUID.randomUUID(), "João", "joao@studium.com", UserRole.DIRECTOR, null, null
                );

        when(userService.createUser(any(), any(), any())).thenReturn(userResp);

        Users userEntity = new Users();
        userEntity.setId(userResp.id());
        userEntity.setName(userResp.name());
        userEntity.setEmail(userResp.email());

        when(userService.findById(userResp.id())).thenReturn(userEntity);

        // save director returns an entity — we can return same director via repository mock
        when(directorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        DirectorResponseDTO res = adminService.createDirector(request);

        assertThat(res).isNotNull();
        assertThat(res.name()).isEqualTo("João");
        verify(userService, times(1)).createUser(any(), any(), any());
        verify(directorRepository, times(1)).save(any());
    }
}
