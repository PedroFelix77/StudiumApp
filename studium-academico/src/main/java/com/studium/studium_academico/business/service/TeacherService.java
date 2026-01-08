package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.response.TeacherResponseDTO;
import com.studium.studium_academico.infrastructure.entity.EntityStatus;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.exceptions.BusinessValidationException;
import com.studium.studium_academico.infrastructure.mapper.TeacherMapper;
import com.studium.studium_academico.infrastructure.repository.TeacherRepository;
import com.studium.studium_academico.infrastructure.entity.Teacher;
import com.studium.studium_academico.infrastructure.exceptions.ResourceNotFoundException;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final UsersRepository usersRepository;

    public Page<TeacherResponseDTO> list(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            return teacherRepository.findAll(pageable).map(teacherMapper::toResponseDTO);
        }
        String qStr = q.trim();
        return teacherRepository.findByUser_NameContainingIgnoreCaseOrUser_EmailContainingIgnoreCaseOrSpecialtyContainingIgnoreCase(
                qStr, qStr, qStr, pageable
        ).map(teacherMapper::toResponseDTO);
    }

    public TeacherResponseDTO findById(UUID id) {
        Teacher t = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + id));
        return teacherMapper.toResponseDTO(t);
    }

    @Transactional
    public void softDelete(UUID id) {
        Teacher t = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + id));

        t.setStatus(EntityStatus.INACTIVE);
        teacherRepository.save(t);

        Users user = t.getUser();
        if (user != null) {
            user.setStatus(EntityStatus.INACTIVE);
            usersRepository.save(user);
        }
    }

    public List<TeacherResponseDTO> findByCourse(UUID courseId) {
        log.info("Buscando professores do curso: {}", courseId);

        try {
            List<Teacher> teachers = teacherRepository.findByCoursesId(courseId);

            if (teachers.isEmpty()) {
                log.warn("Nenhum professor encontrado para o curso: {}", courseId);
                return Collections.emptyList();
            }

            return teachers.stream()
                    .map(teacherMapper::toResponseDTO)
                    .toList();

        } catch (Exception e) {
            log.error("Erro ao buscar professores do curso {}: {}", courseId, e.getMessage());
            throw new BusinessValidationException("Erro ao buscar professores: " + e.getMessage());
        }
    }

}
