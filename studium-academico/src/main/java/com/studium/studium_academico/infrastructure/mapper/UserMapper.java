package com.studium.studium_academico.infrastructure.mapper;

import com.studium.studium_academico.business.dto.request.UserRequestDTO;
import com.studium.studium_academico.business.dto.response.UserResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toResponseDTO(Users user);
    Users toEntity(UserRequestDTO data);
}
