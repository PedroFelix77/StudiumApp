package com.studium.studium_academico.business.service;

import com.studium.studium_academico.business.dto.response.DashboardResponseDTO;
import com.studium.studium_academico.business.dto.response.dashboard.DashboardActivityDTO;
import com.studium.studium_academico.business.dto.response.dashboard.DashboardCoursePerformanceDTO;
import com.studium.studium_academico.business.dto.response.dashboard.DashboardStatDTO;
import com.studium.studium_academico.infrastructure.entity.Course;
import com.studium.studium_academico.infrastructure.entity.StatusFrequency;
import com.studium.studium_academico.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final FrequencyRepository frequencyRepository;
    private final RegistrationRepository registrationRepository;

    public DashboardResponseDTO getAdminDashboard() {
        List<DashboardStatDTO> stats = List.of(
                new DashboardStatDTO("Total de Alunos", studentRepository.count(), null),
                new DashboardStatDTO("Professores Ativos", teacherRepository.findActiveTeachers(), null),
                new DashboardStatDTO("Cursos Ativos", courseRepository.count(), null),
                // Avisos: placeholder
                new DashboardStatDTO("Avisos Pendentes", 5L, null) // Valor fixo temporário
        );

        // Course performance: student count + avg grade
        List<DashboardCoursePerformanceDTO> perf = courseRepository.findAll().stream().map(course -> {
            Long studentCount = registrationRepository.countByCourseId(course.getId());
            Double avg = gradeRepository.findAverageGradeByCourseId(course.getId())
                    .orElse(0.0); // Usando o método corrigido
            return new DashboardCoursePerformanceDTO(
                    course.getId(),
                    course.getName(),
                    studentCount,
                    avg
            );
        }).collect(Collectors.toList());

        List<DashboardActivityDTO> recent = loadRecentActivities();

        return new DashboardResponseDTO(stats, perf, recent);
    }

    public DashboardResponseDTO getDirectorDashboard() {
        return getAdminDashboard();
    }

    public DashboardResponseDTO getTeacherDashboard(UUID teacherId) {
        // stats: total classes this teacher, students taught, avg grade for his courses
        long teacherCourses = courseRepository.findByTeacherId(teacherId).size();
        long studentsTaught = registrationRepository.countByTeacherId(teacherId);
        long upcoming = 0; // placeholder

        List<DashboardStatDTO> stats = List.of(
                new DashboardStatDTO("Cursos ministrados", teacherCourses, null),
                new DashboardStatDTO("Alunos (únicos)", studentsTaught, null),
                new DashboardStatDTO("Aulas próximas", upcoming, null)
        );

        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        List<DashboardCoursePerformanceDTO> perf = courses.stream().map(course -> {
            Long studentCount = registrationRepository.countByCourseId(course.getId());
            Double avg = gradeRepository.findAverageGradeByCourseIdAndTeacherId(course.getId(), teacherId).orElse(0.0);
            return new DashboardCoursePerformanceDTO(course.getId(), course.getName(), studentCount, avg);
        }).collect(Collectors.toList());

        List<DashboardActivityDTO> recent = loadRecentActivitiesForTeacher(teacherId);

        return new DashboardResponseDTO(stats, perf, recent);
    }

    public DashboardResponseDTO getStudentDashboard(UUID studentId) {
        // stats
        long registrations = registrationRepository.countByStudentId(studentId);

        // Corrigido: usando o método correto de FrequencyRepository
        long classesAttended = frequencyRepository.countByStudentIdAndStatus(studentId, StatusFrequency.PRESENT);

        // Corrigido: usando o método correto de GradeRepository
        long pendingGrades = gradeRepository.countByStudentIdAndGradeIsNull(studentId);

        List<DashboardStatDTO> stats = List.of(
                new DashboardStatDTO("Semestres / Matrículas", registrations, null),
                new DashboardStatDTO("Aulas presenciais (reg.)", classesAttended, null),
                new DashboardStatDTO("Notas pendentes", pendingGrades, null)
        );

        // coursePerformance: show avg grade per enrolled course
        List<DashboardCoursePerformanceDTO> perf = registrationRepository.findByStudentId(studentId).stream()
                .map(reg -> {
                    Course c = reg.getCourse();
                    Double avg = gradeRepository.findAverageGradeByCourseIdAndStudentId(c.getId(), studentId).orElse(0.0);
                    long sc = registrationRepository.countByCourseId(c.getId());
                    return new DashboardCoursePerformanceDTO(c.getId(), c.getName(), sc, avg);
                }).collect(Collectors.toList());

        List<DashboardActivityDTO> recent = loadRecentActivitiesForStudent(studentId);

        return new DashboardResponseDTO(stats, perf, recent);
    }

    private List<DashboardActivityDTO> loadRecentActivities() {
        return List.of(
                new DashboardActivityDTO(UUID.randomUUID(),"Nova matrícula: João Silva - Engenharia", LocalDateTime.now().minusHours(2)),
                new DashboardActivityDTO(UUID.randomUUID(),"Notas lançadas: Cálculo II - Turma A", LocalDateTime.now().minusHours(6))
        );
    }

    private List<DashboardActivityDTO> loadRecentActivitiesForTeacher(UUID teacherId) {
        return List.of(
                new DashboardActivityDTO(UUID.randomUUID(),"Você lançou notas na disciplina X", LocalDateTime.now().minusDays(1))
        );
    }

    private List<DashboardActivityDTO> loadRecentActivitiesForStudent(UUID studentId) {
        return List.of(
                new DashboardActivityDTO(UUID.randomUUID(),"Nova nota lançada: Cálculo II", LocalDateTime.now().minusDays(2))
        );
    }
}