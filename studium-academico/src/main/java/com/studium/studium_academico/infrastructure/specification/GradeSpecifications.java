package com.studium.studium_academico.infrastructure.specification;

import com.studium.studium_academico.infrastructure.entity.Grade;
import com.studium.studium_academico.infrastructure.entity.TypeGrade;
import com.studium.studium_academico.infrastructure.mapper.GradeMapper;
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

    public static Specification<Grade> hasTeacher(UUID id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.equal(root.get("teacher").get("id"), id);
    }

    public static Specification<Grade> hasDiscipline(UUID id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.equal(root.get("discipline").get("id"), id);
    }

    public static Specification<Grade> hasClassId(UUID id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.equal(root.get("classEntity").get("id"), id);
    }

    public static Specification<Grade> hasCourse(UUID id) {
        return (root, query, cb) ->
                id == null ? null :
                        cb.equal(root.get("registration").get("course").get("id"), id);
    }

    public static Specification<Grade> hasType(TypeGrade type) {
        return (root, query, cb) ->
                type == null ? null :
                        cb.equal(root.get("typeGrade"), type);
    }
}
