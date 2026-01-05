package com.studium.studium_academico.infrastructure.specification;

import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.mapper.GradeMapper;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class GradeSpecifications {
    public static Specification<Grade> hasRegistration(UUID id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.equal(root.get("registration").get("id"), id);
    }

    public static Specification<Grade> hasStudent(UUID id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.equal(root.get("registration").get("student").get("id"), id);
    }

    public static Specification<Grade> hasTeacher(UUID teacherId) {
        return (root, query, cb) -> {
            if (teacherId == null) return null;

            Join<Grade, Registration> registration = root.join("registration");
            Join<Registration, Classes> classes = registration.join("classEntity");
            Join<Classes, TeacherClass> teacherClass = classes.join("teacherClasses");

            return cb.equal(teacherClass.get("teacher").get("id"), teacherId);
        };
    }

    public static Specification<Grade> hasDiscipline(UUID disciplineId) {
        return (root, query, cb) -> {
            if (disciplineId == null) return null;
            return cb.equal(root.get("discipline").get("id"), disciplineId);
        };
    }

    public static Specification<Grade> hasClassId(UUID classId) {
        return (root, query, cb) -> {
            if (classId == null) return null;

            Join<Grade, Registration> registration = root.join("registration");
            return cb.equal(registration.get("classEntity").get("id"), classId);
        };
    }


    public static Specification<Grade> hasCourse(UUID id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.equal(root.get("registration").get("course").get("id"), id);
    }

    public static Specification<Grade> hasType(TypeGrade type) {
        return (root, query, cb) -> {
            if (type == null) return null;
            return cb.equal(root.get("typeGrade"), type);
        };
    }

}
