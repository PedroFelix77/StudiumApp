package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.response.StudentResponseDTO;
import com.studium.studium_academico.infrastructure.entity.EntityStatus;
import com.studium.studium_academico.infrastructure.entity.Student;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.mapper.StudentMapper;
import com.studium.studium_academico.infrastructure.repository.StudentRepository;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UsersRepository usersRepository;

    public Page<StudentResponseDTO> list(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            return studentRepository.findAll(pageable)
                    .map(studentMapper::toResponseDTO);
        }
        String qStr = q.trim();
        return studentRepository.findByUser_NameContainingIgnoreCaseOrUser_EmailContainingIgnoreCaseOrRegistrationContainingIgnoreCase(
                qStr, qStr, qStr, pageable
        ).map(studentMapper::toResponseDTO);
    }

    public StudentResponseDTO findById(UUID id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + id));
        return studentMapper.toResponseDTO(s);
    }

    @Transactional
    public void softDelete(UUID id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));

        s.setStatus(EntityStatus.INACTIVE);
        studentRepository.save(s);

        Users user = s.getUser();
        if (user != null) {
            user.setStatus(EntityStatus.INACTIVE);
            usersRepository.save(user);
        }
    }

}
