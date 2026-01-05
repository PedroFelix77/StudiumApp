package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.response.GradeResponseDTO;
import com.studium.studium_academico.infrastructure.entity.Grade;
import com.studium.studium_academico.infrastructure.entity.TypeGrade;
import com.studium.studium_academico.infrastructure.mapper.GradeMapper;
import com.studium.studium_academico.infrastructure.repository.GradeRepository;
import com.studium.studium_academico.infrastructure.specification.GradeSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GradeFilterService {
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private GradeMapper mapper;

    public Page<GradeResponseDTO> filterGlobal(
            UUID studentId,
            UUID registrationId,
            UUID disciplineId,
            UUID courseId,
            UUID teacherId,
            UUID classId,
            TypeGrade type,
            Pageable pageable
    ){
        Specification<Grade> spec = Specification.allOf(
                GradeSpecifications.hasStudent(studentId),
                GradeSpecifications.hasRegistration(registrationId),
                GradeSpecifications.hasDiscipline(disciplineId),
                GradeSpecifications.hasCourse(courseId),
                GradeSpecifications.hasTeacher(teacherId),
                GradeSpecifications.hasClassId(classId),
                GradeSpecifications.hasType(type)
        );

        return gradeRepository.findAll(spec, pageable).map(mapper::toDTO);
    }

    public Page<GradeResponseDTO> filterForStudent(
            UUID studentId,
            UUID disciplineId,
            TypeGrade type,
            Pageable pageable
    ) {

        Specification<Grade> spec = Specification.allOf(
                GradeSpecifications.hasStudent(studentId),
                GradeSpecifications.hasDiscipline(disciplineId),
                GradeSpecifications.hasType(type)
        );

        return gradeRepository.findAll(spec, pageable).map(mapper::toDTO);
    }


    public Page<GradeResponseDTO> filterForTeacher(
            UUID teacherId,
            UUID disciplineId,
            UUID classId,
            TypeGrade type,
            Pageable pageable
    ) {

        Specification<Grade> spec = Specification.allOf(
                GradeSpecifications.hasTeacher(teacherId),
                GradeSpecifications.hasDiscipline(disciplineId),
                GradeSpecifications.hasClassId(classId),
                GradeSpecifications.hasType(type)
        );

        return gradeRepository.findAll(spec, pageable).map(mapper::toDTO);
    }

    public Page<GradeResponseDTO> filterForCourse(
            UUID courseId,
            UUID disciplineId,
            UUID classId,
            TypeGrade type,
            Pageable pageable
    ) {

        Specification<Grade> spec = Specification.allOf(
                GradeSpecifications.hasCourse(courseId),
                GradeSpecifications.hasDiscipline(disciplineId),
                GradeSpecifications.hasClassId(classId),
                GradeSpecifications.hasType(type)
        );

        return gradeRepository.findAll(spec, pageable).map(mapper::toDTO);
    }

    public Page<Grade> findGradesByTeacher(UUID teacherId, Pageable pageable) {
        return gradeRepository.findGradesByTeacher(teacherId, pageable);
    }

}
