package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.DepartmentRequestDTO;
import com.studium.studium_academico.business.dto.response.DepartmentResponseDTO;
import com.studium.studium_academico.business.dto.response.DepartmentWithStatsResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Department;
import com.studium.studium_academico.infrastructure.entity.Institution;
import com.studium.studium_academico.infrastructure.repository.DepartmentRepository;
import com.studium.studium_academico.infrastructure.repository.InstitutionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private InstitutionRepository instRepository;

    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO data) {
        Institution institution = instRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Nenhuma instituição encontrada"));

        if (departmentRepository.existsByNameAndInstitutionId(data.name(), institution.getId())) {
            throw new RuntimeException("Já existe algum departamento com o nome " + data.name());
        }

        Department department = Department.builder()
                .name(data.name())
                .institution(institution)
                .courses(new ArrayList<>())
                .teachers(new ArrayList<>())
                .build();

        Department savedDepartment = departmentRepository.save(department);
        return new DepartmentResponseDTO(savedDepartment.getId(), savedDepartment.getName(), savedDepartment.getDescription());
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        Institution institution = instRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Nenhuma instituição encontrada"));

        return departmentRepository.findByInstitutionId(institution.getId())
                .stream()
                .map(dept -> new DepartmentResponseDTO(dept.getId(), dept.getName(), dept.getDescription()))
                .toList();
    }

    public DepartmentResponseDTO findById(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nenhum departamento encontrado"));

        return new DepartmentResponseDTO(department.getId(), department.getName(), department.getDescription());
    }

    @Transactional
    public DepartmentResponseDTO updateDepartment(UUID id, DepartmentRequestDTO data) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Departamento não encontrado"));
        Institution institution = instRepository.findFirstByOrderByIdAsc()
                        .orElseThrow(() -> new EntityNotFoundException("Instituição não encontrada"));

        if (departmentRepository.existsByNameAndInstitutionId(data.name(), institution.getId())) {
            throw new RuntimeException("Já existe um departamento com o nome: " + data.name());
        }

        department.setName(data.name());
        department.setDescription(data.description());

        Department updated =  departmentRepository.save(department);
        return new DepartmentResponseDTO(updated.getId(), updated.getName(), updated.getDescription());
    }

    public List<DepartmentWithStatsResponseDTO> findAllWithStats(){
        Institution institution = instRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Nenhuma instituição encontrada"));

        return departmentRepository.findByInstitutionId(institution.getId())
                .stream()
                .map(dept -> new DepartmentWithStatsResponseDTO(
                        dept.getId(),
                        dept.getName(),
                        dept.getDescription(),
                        dept.getCourses() != null ? dept.getCourses().size() : 0,
                        dept.getTeachers() != null ? dept.getTeachers().size() : 0
                ))
                .toList();
    }

    @Transactional
    public void deleteDepartment(UUID id) {
        Department department = departmentRepository.findByIdWithTeachersAndCourses(id)
                .orElseThrow(() -> new RuntimeException("Nenhum departamento encontrado"));

        StringBuilder errorMessage =  new StringBuilder();

        if (department.getTeachers() != null && !department.getTeachers().isEmpty()) {
            errorMessage.append(String.format("Existem %d professor(es) vinculado(s). ",  department.getTeachers().size()));
        }

        if (department.getCourses() != null && !department.getCourses().isEmpty()) {
            errorMessage.append(String.format("Existem %d curso(s) vinculado(s). ",  department.getCourses().size()));
        }

        if(errorMessage.length()>0){
            throw new RuntimeException("Não é possível excluir o departamento. " + errorMessage.toString());
        }

        departmentRepository.delete(department);
    }

    public Department findDepartmentWithRelations(UUID id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhum departamento encontrado"));
    }

}
