package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.AddressRequestDTO;
import com.studium.studium_academico.business.dto.request.UserRequestDTO;
import com.studium.studium_academico.business.dto.response.UserResponseDTO;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.repository.InstitutionRepository;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import com.studium.studium_academico.infrastructure.mapper.AddressMapper;
import com.studium.studium_academico.infrastructure.mapper.InstitutionMapper;
import com.studium.studium_academico.infrastructure.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private InstitutionRepository institutionRepository;
    @Autowired
    private ActivationService activationService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private InstitutionMapper  institutionMapper;
    @Autowired
    private AddressMapper addressMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO data, AddressRequestDTO addressData, UserRole role) {

        validateUser(data);

        Institution institution = getDefaultInstitution();
        Address address = addressService.createEntity(addressData);
        String tempPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        Users user = Users.builder()
                .name(data.name())
                .email(data.email().toLowerCase().trim())
                .cpf(clean(data.cpf()))
                .birthday(data.birthday())
                .phone(clean(data.phone()))
                .role(role)
                .institution(institution)
                .address(address)
                .password(tempPassword)         // será definida no link de ativação
                .build();
        user.setStatus(EntityStatus.INACTIVE);

        Users saved = usersRepository.save(user);

        activationService.generateAndSendActivationLink(saved);

        return userMapper.toResponseDTO(saved);
    }

    public UserResponseDTO createUserWithPassword(UserRequestDTO data, AddressRequestDTO addressRequestDTO, String password, UserRole role) {

        Address address = addressService.createEntity(addressRequestDTO);
        Institution institution = getDefaultInstitution();
        Users user = Users.builder()
                .name(data.name())
                .email(data.email())
                .cpf(data.cpf())
                .birthday(data.birthday())
                .address(address)
                .phone(data.phone())
                .password(passwordEncoder.encode(password))
                .role(role)
                .institution(institution)
                .build();

        user.setStatus(EntityStatus.ACTIVE);
        Users savedUser = usersRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                institutionMapper.toResponseDTO(savedUser.getInstitution()),
                addressMapper.toResponseDTO(savedUser.getAddress())
        );
    }



    public  UserResponseDTO findByIdResponse(UUID id){
        return userMapper.toResponseDTO(findById(id));
    }

    @Transactional
    public List<UserResponseDTO> findAll(){
        return usersRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public UserResponseDTO updateUser(UserRequestDTO data, UUID id, AddressRequestDTO addressDTO) {
        validateUser(data);
        Users user = findById(id);

        user.setName(data.name());
        user.setEmail(data.email());
        user.setPhone(data.phone());
        if(addressDTO != null){
            addressService.update(user.getAddress().getId(), addressDTO);
        }

        usersRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        log.warn("Soft delete do usuário: {}", id);
        Users user = findById(id);
        user.setStatus(EntityStatus.DELETED);
        user.setUpdatedAt(LocalDateTime.now());
        usersRepository.save(user);
    }
    // validações

    private void validateUser(UserRequestDTO data) {
        if (usersRepository.findByEmail(data.email()).isPresent())
            throw new RuntimeException("Email já cadastrado: " + data.email());

        if (usersRepository.findByCpf(clean(data.cpf())).isPresent())
            throw new RuntimeException("CPF já cadastrado: " + data.cpf());

        if (data.birthday().isAfter(java.time.LocalDate.now().minusYears(16)))
            throw new RuntimeException("Usuário deve ter no mínimo 16 anos");
    }

    //metodos auxiliares

    private Institution getDefaultInstitution() {
        return institutionRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new EntityNotFoundException("Instituição não encontrada"));
    }

    private String clean(String value) {
        return value.replaceAll("[^0-9]", "");
    }

    public Users findById(UUID id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public List<UserResponseDTO> findByRole(UserRole role) {
        log.info("Buscando usuários com role: {}", role);

        return usersRepository.findByRole(role)
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void activateUser(UUID id) {
        log.info("Ativando usuário: {}", id);

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Remove a data de desativação (soft delete)
        deleteUser(null);

        usersRepository.save(user);
        log.info("Usuário {} ativado com sucesso", id);
    }
    @Transactional
    public Users saveUser(Users user) {
        log.info("Salvando usuário: {}", user.getId());
        return usersRepository.save(user);
    }
}
