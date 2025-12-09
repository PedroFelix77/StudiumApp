package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.CreateDirectorRequestDTO;
import com.studium.studium_academico.business.dto.request.UserRequestDTO;
import com.studium.studium_academico.business.dto.response.DirectorResponseDTO;
import com.studium.studium_academico.infrastructure.entity.UserRole;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.DirectorRepository;
import com.studium.studium_academico.infrastructure.mapper.DirectorMapper; // ADICIONE ESTA LINHA
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock private UserService userService;
    @Mock private DirectorRepository directorRepository;
    @Mock private DirectorMapper directorMapper; // ADICIONE ESTA LINHA

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

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
        userEntity.setCpf("11122233344"); // ADICIONE CPF PARA O TESTE

        when(userService.findById(userResp.id())).thenReturn(userEntity);

        // Cria um objeto Director mock
        com.studium.studium_academico.infrastructure.entity.Director directorEntity =
                com.studium.studium_academico.infrastructure.entity.Director.builder()
                        .user(userEntity)
                        .hireDate(request.hireDate())
                        .build();

        when(directorRepository.save(any())).thenReturn(directorEntity);

        // Cria o response DTO esperado
        DirectorResponseDTO expectedResponse = new DirectorResponseDTO(
                directorEntity.getId(),
                "João",
                "11122233344",
                "joao@studium.com"
        );

        // CONFIGURE O MOCK DO DIRECTOR MAPPER
        when(directorMapper.toResponseDTO(any())).thenReturn(expectedResponse);

        DirectorResponseDTO res = adminService.createDirector(request);

        assertThat(res).isNotNull();
        assertThat(res.name()).isEqualTo("João");
        verify(userService, times(1)).createUser(any(), any(), any());
        verify(directorRepository, times(1)).save(any());
        // VERIFIQUE SE O MAPPER FOI CHAMADO
        verify(directorMapper, times(1)).toResponseDTO(any());
    }
}