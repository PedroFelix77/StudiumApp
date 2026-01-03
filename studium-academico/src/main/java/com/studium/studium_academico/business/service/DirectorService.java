package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.request.CreateStudentRequestDTO;
import com.studium.studium_academico.business.dto.request.CreateTeacherRequestDTO;
import com.studium.studium_academico.business.dto.response.StudentResponseDTO;
import com.studium.studium_academico.business.dto.response.TeacherResponseDTO;
import com.studium.studium_academico.business.dto.response.UserResponseDTO;
import com.studium.studium_academico.infrastructure.entity.*;
import com.studium.studium_academico.infrastructure.repository.*;
import com.studium.studium_academico.infrastructure.mapper.StudentMapper;
import com.studium.studium_academico.infrastructure.mapper.TeacherMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    @Autowired
    private UserService userService;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DisciplineRepository disciplineRepository;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Transactional
    public TeacherResponseDTO createTeacher(CreateTeacherRequestDTO data) {
        log.info("CREATE TEACHER PAYLOAD: {}", data);
        UserResponseDTO userResponseDTO = userService.createUser(
                data.user(),
                data.address(),
                UserRole.TEACHER
        );
        Users user = userService.findById(userResponseDTO.id());

        Department department = validateAndGetDepartment(data.departmentId());

        List<Course> courses = new ArrayList<>();
        if(data.courseIds() != null && !data.courseIds().isEmpty()) {
            courses = validateAndGetCourses(data.courseIds());
        }

        Teacher teacher = Teacher.builder()
                .user(user)
                .department(department)
                .courses(courses)
                .disciplines(new ArrayList<>())
                .teacherClasses(new ArrayList<>())
                .registeredFrequencies(new ArrayList<>())
                .hireDate(data.hireDate() != null ? data.hireDate() : LocalDate.now())
                .specialty(data.specialty())
                .build();

        Teacher savedTeacher = teacherRepository.save(teacher);

        user.setTeacher(savedTeacher);
        userService.saveUser(user);

        return teacherMapper.toResponseDTO(savedTeacher);
    }

    @Transactional
    public StudentResponseDTO createStudent(CreateStudentRequestDTO data) {
        log.info("[DIRECTOR] Criando aluno: {}", data.user().email());

        UserResponseDTO userResponse = userService.createUser(
                data.user(),
                data.address(),
                UserRole.STUDENT
        );

        Users user = userService.findById(userResponse.id());

        Student student = Student.builder()
                .user(user)
                .responsibleName(data.responsibleName())
                .responsiblePhone(data.responsiblePhone())
                .registrations(new ArrayList<>())
                .build();

        Student savedStudent = studentRepository.save(student);

        user.setStudent(savedStudent);
        userService.saveUser(user);

        return studentMapper.toResponseDTO(savedStudent);
    }

    private Department validateAndGetDepartment(UUID departmentId) {
        if (departmentId == null) {
            throw new RuntimeException("Departamento é obrigatório para professor");
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado"));

        // Validação: Verificar se o department pertence à instituição do diretor
        validateInstitutionAccess(department.getInstitution());

        return department;
    }

    private List<Course> validateAndGetCourses(List<UUID> courseIds) {
        List<Course> courses = courseRepository.findAllById(courseIds);

        if (courses.size() != courseIds.size()) {
            throw new RuntimeException("Um ou mais cursos não foram encontrados");
        }
        courses.forEach(course -> validateInstitutionAccess(course.getInstitution()));

        return courses;
    }

    private List<Discipline> validateAndGetDisciplines(List<UUID> disciplineIds) {
        List<Discipline> disciplines = disciplineRepository.findAllById(disciplineIds);

        if (disciplines.size() != disciplineIds.size()) {
            throw new RuntimeException("Uma ou mais disciplinas não foram encontradas");
        }

        return disciplines;
    }

    private void validateInstitutionAccess(Institution institution) {
        // ✅ Implementar: Verificar se a instituição é a mesma do diretor logado
        // Por enquanto, como temos apenas uma instituição, não precisa de validação
        // No futuro: getCurrentDirectorInstitution() e comparar com institution
    }
}
