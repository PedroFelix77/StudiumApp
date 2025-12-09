package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.AuthLoginRequestDTO;
import com.studium.studium_academico.business.dto.response.AuthLoginResponseDTO;
import com.studium.studium_academico.business.dto.response.UserResponseDTO;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import com.studium.studium_academico.infrastructure.security.TokenService;
import com.studium.studium_academico.infrastructure.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private UsersRepository repository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    @Lazy
    AuthenticationManager authenticationManager;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }


        public AuthLoginResponseDTO login(AuthLoginRequestDTO data){
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.email(), data.password())
            );

            Users user = (Users) auth.getPrincipal();
            String token = tokenService.generateToken(user);

            UserResponseDTO responseUser = userMapper.toResponseDTO(user);

            return new AuthLoginResponseDTO(
                    token, responseUser
            );
        }


}
