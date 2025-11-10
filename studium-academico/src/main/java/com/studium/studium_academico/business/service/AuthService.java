package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.AuthLoginRequestDTO;
import com.studium.studium_academico.business.dto.request.AuthRegisterRequestDTO;
import com.studium.studium_academico.business.dto.response.AuthLoginResponseDTO;
import com.studium.studium_academico.business.dto.response.AuthRegisterResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Address;
import com.studium.studium_academico.infrastructure.entity.Institution;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.InstitutionRepository;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import com.studium.studium_academico.infrastructure.security.TokenService;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private UsersRepository repository;
    @Autowired
    private AddressService addressService;
    @Autowired
    InstitutionRepository instRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email);
    }

    @Transactional
    public AuthLoginResponseDTO login(AuthLoginRequestDTO data, AuthenticationManager authenticationManager){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.email(), data.password())
        );

        Users user = (Users) auth.getPrincipal();
        String token = tokenService.generateToken(user);

        return new AuthLoginResponseDTO(token);
    }

    @Transactional
    public AuthRegisterResponseDTO register(AuthRegisterRequestDTO data) {
        if (repository.findByEmail(data.email()) != null) {
            throw new RuntimeException("Email já cadastrado");
        }

        Address address = addressService.create(data.address());

        Institution institution = instRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Nenhuma instituição encontrada"));

        String encryptedPassword = passwordEncoder.encode(data.password());

                Users user = Users.builder()
                        .name(data.name())
                        .cpf(data.cpf())
                        .email(data.email())
                        .password(encryptedPassword)
                        .birthday(data.birthday())
                        .role(data.role())
                        .address(address)
                        .institution(institution)
                        .build();

                repository.save(user);

                return new AuthRegisterResponseDTO(user.getName(), user.getEmail(), user.getRole());
    }
}
