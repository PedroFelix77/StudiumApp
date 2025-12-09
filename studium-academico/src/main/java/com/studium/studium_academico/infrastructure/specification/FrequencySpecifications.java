package com.studium.studium_academico.infrastructure.specification;

import com.studium.studium_academico.infrastructure.entity.Frequency;
import com.studium.studium_academico.infrastructure.entity.StatusFrequency;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class FrequencySpecifications {

    public static Specification<Frequency> hasStudent(UUID studentId) {
        return (root, query, cb) -> studentId == null ?
                cb.conjunction() :
                cb.equal(root.get("registration").get("student").get("id"), studentId);
    }

    public static Specification<Frequency> hasDiscipline(UUID disciplineId) {
        return (root, query, cb) -> disciplineId == null ?
                cb.conjunction() :
                cb.equal(root.get("classroom").get("discipline").get("id"), disciplineId);
    }

    public static Specification<Frequency> hasCourse(UUID courseId) {
        return (root, query, cb) -> courseId == null ?
                cb.conjunction() :
                cb.equal(root.get("registration").get("course").get("id"), courseId);
    }

    public static Specification<Frequency> hasTeacher(UUID teacherId) {
        return (root, query, cb) -> teacherId == null ?
                cb.conjunction() :
                cb.equal(root.get("registeredByTeacher").get("id"), teacherId);
    }

    public static Specification<Frequency> hasStatus(StatusFrequency status) {
        return (root, query, cb) -> status == null ?
                cb.conjunction() :
                cb.equal(root.get("statusFrequency"), status);
    }

    public static Specification<Frequency> betweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return cb.conjunction();
            if (startDate != null && endDate != null) {
                return cb.between(root.get("attendanceDate"), startDate, endDate);
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("attendanceDate"), startDate);
            }
            return cb.lessThanOrEqualTo(root.get("attendanceDate"), endDate);
        };
    }

    public static Specification<Frequency> hasClassId(UUID classId) {
        return (root, query, cb) -> classId == null ?
                cb.conjunction() :
                cb.equal(root.get("classroom").get("classEntity").get("id"), classId);
    }

    public static Specification<Frequency> hasRegistration(UUID registrationId) {
        return (root, query, cb) ->
                registrationId == null ?
                        cb.conjunction() :
                        cb.equal(root.get("registration").get("id"), registrationId);
    }

    public static Specification<Frequency> hasClassroom(UUID classroomId) {
        return (root, query, cb) ->
                classroomId == null ?
                        cb.conjunction() :
                        cb.equal(root.get("classroom").get("id"), classroomId);
    }
}
