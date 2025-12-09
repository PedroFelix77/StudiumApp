package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.*;
import com.studium.studium_academico.business.dto.response.AdminResponseDTO;
import com.studium.studium_academico.business.dto.response.DirectorResponseDTO;
import com.studium.studium_academico.business.dto.response.UserResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Admin;
import com.studium.studium_academico.infrastructure.entity.Director;
import com.studium.studium_academico.infrastructure.entity.UserRole;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.AdminRepository;
import com.studium.studium_academico.infrastructure.repository.DirectorRepository;
import com.studium.studium_academico.infrastructure.mapper.DirectorMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private UserService userService;
    @Autowired
    private DirectorRepository directorRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DirectorMapper directorMapper;

    @Transactional
    public DirectorResponseDTO createDirector(CreateDirectorRequestDTO data) {
        log.info("[admin] Creating director");
        UserResponseDTO userResponse = userService.createUser(
                data.user(),
                data.address(),
                UserRole.DIRECTOR
        );

        Users user = userService.findById(userResponse.id());

        Director director = Director.builder()
                .user(user)
                .hireDate(data.hireDate() != null ? data.hireDate() : LocalDate.now())
                .build();

        Director savedDirector = directorRepository.save(director);

        return directorMapper.toResponseDTO(savedDirector);
    }

    @Transactional
    public AdminResponseDTO createAdmin(CreateAdminRequestDTO data) {

        log.info("[admin] Creating ADMIN");

        UserRequestDTO userData = new UserRequestDTO(
                data.name(),
                data.cpf(),
                data.email(),
                data.birthday(),
                data.phone(),
                data.institutionId()
        );
        // Cria usuário já com senha
        UserResponseDTO userResponse = userService.createUserWithPassword(
                userData,
                data.address(),
                data.password(),
                UserRole.ADMIN
        );

        Users user = userService.findById(userResponse.id());

        Admin admin = Admin.builder()
                .user(user)
                .hireDate(data.hireDate() != null ? data.hireDate() : LocalDate.now())
                .build();

        Admin savedAdmin = adminRepository.save(admin);

        return new AdminResponseDTO(
                savedAdmin.getId(),
                user.getName(),
                user.getEmail()
        );
    }


    public UserResponseDTO findUserById(UUID id) {
        return userService.findByIdResponse(id);
    }

    public List<UserResponseDTO> findAllUsers() {
        return userService.findAll();
    }

    @Transactional
    public UserResponseDTO updateUser(
            UUID id,
            UserRequestDTO data,
            AddressRequestDTO address
    ) {
        log.info("[ADMIN] Atualizando usuário {}", id);
        return userService.updateUser(data, id, address);
    }

    @Transactional
    public void deleteUser(UUID id) {
        log.warn("[ADMIN] Excluindo usuário {}", id);
        userService.deleteUser(id);
    }

    @Transactional
    public void activateUser(UUID id) {
        log.info("[ADMIN] Reativando usuário {}", id);
        userService.activateUser(id);
    }

    public List<UserResponseDTO> findUsersByRole(UserRole role) {
        return userService.findByRole(role);
    }

}
